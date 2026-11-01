package com.chassisui.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chassisui.example.chassis.ChassisFonts
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisMotion
import com.chassisui.example.chassis.ChassisTheme
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.Icon
import com.chassisui.example.components.Badge
import com.chassisui.example.components.NotificationBanner
import com.chassisui.example.screens.AccountView
import com.chassisui.example.screens.FoundationsView
import com.chassisui.example.screens.HomeView
import com.chassisui.example.screens.InboxView
import com.chassisui.example.screens.OnboardingView
import com.chassisui.example.screens.SearchView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChassisFonts.check(resources)
        setContent {
            RootView()
        }
    }
}

/** Sign-in first, then the tabs. The choice of appearance applies to both. */
@Composable
private fun RootView() {
    val model = viewModel<AppModel>()
    ChassisTheme(model.appearance) {
        CompositionLocalProvider(LocalAppModel provides model) {
            Box(Modifier.fillMaxSize().background(colorResource(R.color.color_context_default_bg_even))) {
                if (model.isSignedIn) {
                    MainTabs()
                } else {
                    OnboardingView()
                }
                ToastOverlay(model.toast)
            }
        }
    }
}

/** It sits over the bar for a moment, and the buttons under it keep working. */
@Composable
private fun ToastOverlay(toast: Toast?) {
    val spec = ChassisMotion.standard<Float>()
    val offsetSpec = ChassisMotion.standard<IntOffset>()
    AnimatedContent(
        targetState = toast,
        transitionSpec = {
            (slideInVertically(offsetSpec) { -it } + fadeIn(spec))
                .togetherWith(slideOutVertically(offsetSpec) { -it } + fadeOut(spec))
        },
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        label = "toast",
    ) { current ->
        if (current != null) {
            NotificationBanner(
                current,
                Modifier.padding(horizontal = dimensionResource(R.dimen.space_context_medium)),
            )
        }
    }
}

private enum class MainTab(val title: String, val icon: ChassisIcon) {
    Home("Home", ChassisIcon.Home),
    Search("Search", ChassisIcon.Search),
    Inbox("Inbox", ChassisIcon.Inbox),
    Account("Account", ChassisIcon.Account),
    Foundations("Foundations", ChassisIcon.Foundations),
}

/** `Mobile Bottom Navigation`, type tabs: the native bar, styled by tokens. Each tab keeps its stack. */
@Composable
private fun MainTabs() {
    val model = LocalAppModel.current
    var selected by rememberSaveable { mutableStateOf(MainTab.Home) }
    val stacks = rememberSaveableStateHolder()

    Scaffold(
        containerColor = colorResource(R.color.color_context_default_bg_even),
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = dimensionResource(R.dimen.size_asset_separator),
                    color = colorResource(R.color.color_context_default_border_subtle),
                )
                NavigationBar(containerColor = colorResource(R.color.color_context_default_bg_even)) {
                    MainTab.entries.forEach { tab ->
                        val unread = if (tab == MainTab.Inbox) model.unreadCount else 0
                        NavigationBarItem(
                            selected = tab == selected,
                            onClick = { selected = tab },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unread > 0) {
                                            Badge(unread.toString())
                                        }
                                    },
                                    modifier = Modifier.semantics {
                                        if (unread > 0) {
                                            contentDescription = "$unread unread"
                                        }
                                    },
                                ) {
                                    Icon(tab.icon, dimensionResource(R.dimen.size_icon_glyph_medium))
                                }
                            },
                            label = {
                                Text(
                                    tab.title,
                                    style = ChassisType.tab.style,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(R.color.color_context_primary_base_color),
                                selectedTextColor = colorResource(R.color.color_context_primary_base_color),
                                indicatorColor = colorResource(R.color.color_context_default_transparent_color),
                                unselectedIconColor = colorResource(R.color.color_context_default_fg_subtle),
                                unselectedTextColor = colorResource(R.color.color_context_default_fg_subtle),
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        // Only the bar at the bottom: each screen has a bar of its own under the status bar
        val bottom = PaddingValues(bottom = padding.calculateBottomPadding())
        Box(Modifier.padding(bottom).consumeWindowInsets(bottom)) {
            stacks.SaveableStateProvider(selected.name) {
                when (selected) {
                    MainTab.Home -> HomeView()
                    MainTab.Search -> SearchView()
                    MainTab.Inbox -> InboxView()
                    MainTab.Account -> AccountView()
                    MainTab.Foundations -> FoundationsView()
                }
            }
        }
    }
}
