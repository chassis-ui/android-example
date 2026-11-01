import groovy.json.JsonSlurper
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.zip.ZipFile
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

/**
 * Fills Generated/res with the `res/` folders of the tokens and the assets of one brand, at the
 * versions that gradle.properties pins. Gradle runs it again only when one of those settings changed.
 *
 * Every download is checked against the digest GitHub records for it, and kept in .cache/.
 * Nothing is written to Generated/ until both downloads have passed and the brand is known.
 */
@DisableCachingByDefault(because = "The downloads have their own cache")
abstract class SyncBrand : DefaultTask() {
    @get:Input abstract val brand: Property<String>
    @get:Input abstract val tokensRepository: Property<String>
    @get:Input abstract val tokensVersion: Property<String>
    @get:Input abstract val tokensArchive: Property<String>
    @get:Input abstract val assetsRepository: Property<String>
    @get:Input abstract val assetsVersion: Property<String>
    @get:Input abstract val assetsArchive: Property<String>

    // Without a token GitHub allows 60 calls an hour per address, which a shared runner can use up
    @get:Internal abstract val gitHubToken: Property<String>
    @get:Internal abstract val cacheDirectory: DirectoryProperty
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun sync() {
        val cache = cacheDirectory.get().asFile.apply { mkdirs() }
        val output = outputDirectory.get().asFile
        // On the volume of Generated/, so that moving the result there is one rename
        val work = Files.createTempDirectory(cache.toPath(), "work").toFile()
        try {
            // Both first: a failed download leaves Generated/ as it is
            val tokensZip = fetch(tokensRepository.get(), tokensVersion.get(), tokensArchive.get(), cache)
            val assetsZip = fetch(assetsRepository.get(), assetsVersion.get(), assetsArchive.get(), cache)

            val res = File(work, "res")
            for (archive in listOf(tokensZip, assetsZip)) {
                val unpacked = File(work, archive.name).also { unzip(archive, it) }
                val folder = File(unpacked, "res")
                if (!folder.isDirectory) throw GradleException("${archive.name} has no res/ folder")
                // The assets come second, so they win over a file that the tokens hold too
                folder.copyRecursively(res, overwrite = true)
            }

            output.deleteRecursively()
            output.parentFile.mkdirs()
            Files.move(res.toPath(), output.toPath(), StandardCopyOption.ATOMIC_MOVE)
            logger.lifecycle("Generated/res now holds ${brand.get()}: ${tokensArchive.get()}, ${assetsArchive.get()}")
        } finally {
            work.deleteRecursively()
        }
    }

    /** The path in .cache/ of an archive of a release, after downloading and checking it once. */
    private fun fetch(repository: String, version: String, archive: String, cache: File): File {
        val cached = File(cache, "${repository.replace('/', '-')}-$archive")
        if (cached.isFile) return cached

        logger.lifecycle("Downloading $archive from $repository")
        val release = get("https://api.github.com/repos/$repository/releases/tags/v$version", gitHubToken.orNull)
            ?: throw GradleException(
                "Cannot read release v$version of $repository: it does not exist, or GitHub's " +
                    "rate limit was reached. Setting GH_TOKEN raises the limit",
            )
        val assets = (JsonSlurper().parse(release) as Map<*, *>)["assets"] as List<*>
        val asset = assets.map { it as Map<*, *> }.firstOrNull { it["name"] == archive }
            ?: throw GradleException(
                "$repository v$version has no $archive. It has: " +
                    assets.joinToString(" ") { (it as Map<*, *>)["name"].toString() },
            )
        val bytes = get(asset["browser_download_url"].toString(), null)
            ?: throw GradleException("Cannot download $archive")
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
        if ("sha256:$digest" != asset["digest"]) {
            throw GradleException("$archive does not match the digest of its release")
        }
        cached.writeBytes(bytes)
        return cached
    }

    private fun get(url: String, token: String?): ByteArray? {
        val request = HttpRequest.newBuilder(URI(url))
        if (!token.isNullOrEmpty()) request.header("Authorization", "Bearer $token")
        val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
        val response = client.send(request.build(), HttpResponse.BodyHandlers.ofByteArray())
        return response.body().takeIf { response.statusCode() == 200 }
    }

    private fun unzip(archive: File, into: File) {
        ZipFile(archive).use { zip ->
            for (entry in zip.entries()) {
                val file = File(into, entry.name)
                if (!file.toPath().normalize().startsWith(into.toPath())) {
                    throw GradleException("${archive.name} has a path outside its folder: ${entry.name}")
                }
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile.mkdirs()
                    zip.getInputStream(entry).use { input -> file.outputStream().use { input.copyTo(it) } }
                }
            }
        }
    }
}
