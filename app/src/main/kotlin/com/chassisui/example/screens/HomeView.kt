package com.chassisui.example.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chassisui.example.Guide
import com.chassisui.example.GuideCategory
import com.chassisui.example.LocalAppModel
import com.chassisui.example.R
import com.chassisui.example.SampleData
import com.chassisui.example.Toast
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisMotion
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.rememberPageTransitions
import com.chassisui.example.components.ActionBar
import com.chassisui.example.components.BarButton
import com.chassisui.example.components.ButtonKind
import com.chassisui.example.components.ChassisButton
import com.chassisui.example.components.FullWidthImage
import com.chassisui.example.components.GuideCard
import com.chassisui.example.components.InlineBar
import com.chassisui.example.components.LargeBar
import com.chassisui.example.components.SegmentBar
import com.chassisui.example.components.Skeleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** The stack of the Home tab: a feed, and a push to a detail screen. */
@Composable
fun HomeView() {
    val navController = rememberNavController()
    val transitions = rememberPageTransitions()
    NavHost(
        navController,
        startDestination = "feed",
        enterTransition = transitions.enter,
        exitTransition = transitions.exit,
        popEnterTransition = transitions.popEnter,
        popExitTransition = transitions.popExit,
    ) {
        composable("feed") {
            FeedScreen(onOpen = { navController.navigate("guide/${it.id}") })
        }
        composable("guide/{id}") { entry ->
            GuideDetailView(guideById(entry.arguments?.getString("id")), navController)
        }
    }
}

fun guideById(id: String?): Guide = SampleData.guides.first { it.id == id }

/** A feed: a filter, a loading state and pull to refresh. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedScreen(onOpen: (Guide) -> Unit) {
    val model = LocalAppModel.current
    var category by rememberSaveable { mutableStateOf(GuideCategory.All) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val loadTime = integerResource(R.integer.motion_timing_context_3xlarge).toLong()
    val guides = SampleData.guides.filter { category == GuideCategory.All || it.category == category }
    // Each filter starts at its first card, and not at a card it has in common with the last one
    val list = rememberSaveable(category, saver = LazyListState.Saver) { LazyListState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        if (isLoading) {
            delay(loadTime)
            isLoading = false
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colorResource(R.color.color_context_default_bg_even),
        topBar = {
            LargeBar("Discover", scrollBehavior) {
                BarButton(ChassisIcon.Bell, "Announcements", {
                    model.show(Toast(title = "Nothing new", message = "You have read every announcement."))
                })
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding).consumeWindowInsets(padding)) {
            SegmentBar(
                options = GuideCategory.entries.map { it to it.title },
                selection = category,
                onSelect = { category = it },
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.space_context_medium))
                    .padding(bottom = dimensionResource(R.dimen.space_unit_10)),
            )
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        isLoading = true
                        delay(loadTime)
                        isLoading = false
                        isRefreshing = false
                    }
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                Crossfade(isLoading, animationSpec = ChassisMotion.standard(), label = "loading") { loading ->
                    if (loading) {
                        FeedSkeleton()
                    } else {
                        LazyColumn(
                            state = list,
                            contentPadding = PaddingValues(dimensionResource(R.dimen.space_context_medium)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_medium)),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(guides, key = { it.id }) { guide ->
                                GuideCard(guide, onClick = { onOpen(guide) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedSkeleton() {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_medium)),
        modifier = Modifier
            .fillMaxSize()
            // A pull starts on this too
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.space_context_medium)),
    ) {
        repeat(2) {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall))) {
                Skeleton(height = dimensionResource(R.dimen.size_unit_160))
                Skeleton()
                Skeleton()
            }
        }
    }
}

/** A pushed screen, with a save action in the bar and an `Action Bar` at the bottom. */
@Composable
fun GuideDetailView(guide: Guide, navController: NavController) {
    val model = LocalAppModel.current
    val isSaved = guide.id in model.saved
    Scaffold(
        containerColor = colorResource(R.color.color_context_default_bg_main),
        topBar = {
            InlineBar("", onBack = { navController.popBackStack() }) {
                BarButton(
                    if (isSaved) ChassisIcon.HeartFilled else ChassisIcon.Heart,
                    if (isSaved) "Remove from saved" else "Save",
                    { model.toggleSaved(guide) },
                )
            }
        },
        bottomBar = {
            ActionBar {
                ChassisButton(
                    "Back",
                    Modifier.weight(1f),
                    kind = ButtonKind.Outline,
                    onClick = { navController.popBackStack() },
                )
                ChassisButton(
                    if (isSaved) "Saved" else "Save",
                    Modifier.weight(1f),
                    onClick = { model.toggleSaved(guide) },
                )
            }
        },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_medium)),
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            FullWidthImage(guide.image)
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall)),
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.space_context_medium)),
            ) {
                Text(
                    guide.title,
                    style = ChassisType.h2.style,
                    color = colorResource(R.color.color_context_default_fg_main),
                )
                Text(
                    guide.summary,
                    style = ChassisType.lead.style,
                    color = colorResource(R.color.color_context_default_fg_subtle),
                )
                Text(
                    guide.body,
                    style = ChassisType.bodyMedium.style,
                    color = colorResource(R.color.color_context_default_fg_main),
                )
            }
        }
    }
}
