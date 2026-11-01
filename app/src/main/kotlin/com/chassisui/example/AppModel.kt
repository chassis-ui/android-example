package com.chassisui.example

import android.app.Application
import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chassisui.example.chassis.ChassisImage
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class GuideCategory {
    All, Tokens, Assets, Parts;

    val title: String get() = name
}

data class Guide(
    val id: String,
    val title: String,
    val summary: String,
    val body: String,
    val category: GuideCategory,
    val image: ChassisImage,
)

data class ChatMessage(
    val text: String,
    val isMine: Boolean,
    val id: String = UUID.randomUUID().toString(),
)

data class ChatThread(
    val id: String,
    val name: String,
    val messages: List<ChatMessage>,
    val unread: Int,
)

data class Plan(
    val id: String,
    val title: String,
    val price: String,
    val features: List<String>,
)

data class Toast(
    val title: String,
    val message: String,
    val isError: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
)

enum class AppearanceChoice {
    System, Light, Dark
}

/** What the screens share. A real app would read it from a server. */
class AppModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences("app", Context.MODE_PRIVATE)

    var isSignedIn by mutableStateOf(preferences.getBoolean("isSignedIn", false))
        private set
    var appearance by mutableStateOf(
        AppearanceChoice.valueOf(preferences.getString("appearance", null) ?: AppearanceChoice.System.name),
    )
        private set
    var notificationsOn by mutableStateOf(preferences.getBoolean("notificationsOn", true))
        private set

    var saved by mutableStateOf(emptySet<String>())
        private set
    var threads by mutableStateOf(SampleData.threads)
        private set
    var plan by mutableStateOf(SampleData.plans[0])
    var toast by mutableStateOf<Toast?>(null)
        private set

    val unreadCount: Int get() = threads.sumOf { it.unread }

    fun updateSignedIn(value: Boolean) {
        isSignedIn = value
        preferences.edit { putBoolean("isSignedIn", value) }
    }

    fun updateAppearance(value: AppearanceChoice) {
        appearance = value
        preferences.edit { putString("appearance", value.name) }
    }

    fun updateNotificationsOn(value: Boolean) {
        notificationsOn = value
        preferences.edit { putBoolean("notificationsOn", value) }
    }

    fun toggleSaved(guide: Guide) {
        if (guide.id in saved) {
            saved = saved - guide.id
        } else {
            saved = saved + guide.id
            show(Toast(title = "Saved", message = "${guide.title} is in your saved guides."))
        }
    }

    fun show(newToast: Toast) {
        toast = newToast
        viewModelScope.launch {
            delay(getApplication<Application>().resources.getInteger(R.integer.motion_timing_carousel_duration).toLong())
            if (toast == newToast) {
                toast = null
            }
        }
    }

    fun send(text: String, threadId: String) {
        threads = threads.map {
            if (it.id == threadId) it.copy(messages = it.messages + ChatMessage(text, isMine = true)) else it
        }
    }

    fun markRead(threadId: String) {
        threads = threads.map { if (it.id == threadId) it.copy(unread = 0) else it }
    }

    fun delete(threadId: String) {
        threads = threads.filter { it.id != threadId }
    }
}

/** The model of the app, for the screens under `RootView`. */
val LocalAppModel = compositionLocalOf<AppModel> { error("No AppModel here") }

/** The content of the example, which says the same in every brand. */
object SampleData {
    val guides = listOf(
        Guide(
            id = "tokens",
            title = "Design tokens",
            summary = "One set of decisions, written for every platform.",
            body = "Colors, type, spacing and motion come from one release. This app pins its version, so a new release is a one-line change that shows up as a clear diff.",
            category = GuideCategory.Tokens,
            image = ChassisImage.Tokens,
        ),
        Guide(
            id = "library",
            title = "The Figma library",
            summary = "Components that designers and this code both read.",
            body = "The bars of this app follow the mobile navigation of the Figma library, and take the tokens that its pages list.",
            category = GuideCategory.Parts,
            image = ChassisImage.Library,
        ),
        Guide(
            id = "gallery",
            title = "Component gallery",
            summary = "Buttons, fields and cards in all their states.",
            body = "Each component takes its colors, sizes and fonts from tokens. Where the tokens have no group for it, it takes the closest context or unit token.",
            category = GuideCategory.Parts,
            image = ChassisImage.Gallery,
        ),
        Guide(
            id = "platforms",
            title = "Every platform",
            summary = "The same assets, named the way each platform expects.",
            body = "An archive per platform, app and brand holds the fonts, images and icons. This app takes the Android archive with its resources.",
            category = GuideCategory.Assets,
            image = ChassisImage.Platforms,
        ),
        Guide(
            id = "parts",
            title = "Built from parts",
            summary = "Change the brand, and the screens stay the same.",
            body = "Nothing in the source of this app names a brand. One build setting chooses the tokens and the assets, and the code does not change.",
            category = GuideCategory.Assets,
            image = ChassisImage.Parts,
        ),
        Guide(
            id = "docs",
            title = "Documented",
            summary = "What each part is for, and how to use it.",
            body = "This app is an example to read. The README explains how it takes its tokens and assets, and what to copy into a project.",
            category = GuideCategory.Parts,
            image = ChassisImage.Docs,
        ),
    )

    val threads = listOf(
        ChatThread(
            id = "design",
            name = "Maya Chen",
            messages = listOf(
                ChatMessage("The new token release is out.", isMine = false),
                ChatMessage("Great, I'll update the version and build.", isMine = true),
                ChatMessage("Can you check the dark logo too?", isMine = false),
            ),
            unread = 1,
        ),
        ChatThread(
            id = "review",
            name = "Sam Rivera",
            messages = listOf(
                ChatMessage("Is the pricing sheet ready for review?", isMine = false),
                ChatMessage("Yes, it's on the Account tab.", isMine = true),
            ),
            unread = 0,
        ),
        ChatThread(
            id = "support",
            name = "Alex Kim",
            messages = listOf(
                ChatMessage("Which brand does this build use?", isMine = false),
                ChatMessage("Fonts look right to me now.", isMine = false),
            ),
            unread = 2,
        ),
    )

    val plans = listOf(
        Plan(
            id = "starter",
            title = "Starter",
            price = "Free",
            features = listOf("One project", "Community support"),
        ),
        Plan(
            id = "team",
            title = "Team",
            price = "$12 a month",
            features = listOf("Unlimited projects", "Shared libraries", "Email support"),
        ),
        Plan(
            id = "business",
            title = "Business",
            price = "$29 a month",
            features = listOf("Everything in Team", "Single sign-on", "Priority support"),
        ),
    )
}
