plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

fun setting(name: String) = providers.gradleProperty(name).get()

val brandName = setting("CHASSIS_BRAND")

/** Fills Generated/ with the tokens and the assets of the brand, before anything reads them. */
val syncBrand = tasks.register<SyncBrand>("syncBrand") {
    fun archive(name: String, version: String) = setting(name)
        .replace("{app}", setting("CHASSIS_APP"))
        .replace("{brand}", brandName)
        .replace("{version}", version)

    brand = brandName
    tokensRepository = setting("CHASSIS_TOKENS_REPOSITORY")
    tokensVersion = setting("CHASSIS_TOKENS_VERSION")
    tokensArchive = archive("CHASSIS_TOKENS_ARCHIVE", setting("CHASSIS_TOKENS_VERSION"))
    assetsRepository = setting("CHASSIS_ASSETS_REPOSITORY")
    assetsVersion = setting("CHASSIS_ASSETS_VERSION")
    assetsArchive = archive("CHASSIS_ASSETS_ARCHIVE", setting("CHASSIS_ASSETS_VERSION"))
    gitHubToken = providers.environmentVariable("GH_TOKEN")
    cacheDirectory = rootProject.layout.projectDirectory.dir(".cache")
    outputDirectory = rootProject.layout.projectDirectory.dir("Generated/res")
}

android {
    namespace = "com.chassisui.example"
    compileSdk = 37

    defaultConfig {
        // One application id per brand, so that the brands install side by side
        applicationId = "com.chassisui.example.$brandName"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        resValue("string", "app_name", brandName)
    }

    buildFeatures {
        compose = true
        resValues = true
    }

    sourceSets["main"].res.directories.add(rootProject.layout.projectDirectory.dir("Generated/res").asFile.path)
}

tasks.named("preBuild") {
    dependsOn(syncBrand)
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
}
