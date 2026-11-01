package com.chassisui.example.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chassisui.example.Guide
import com.chassisui.example.GuideCategory
import com.chassisui.example.R
import com.chassisui.example.SampleData
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.rememberPageTransitions
import com.chassisui.example.components.Chip
import com.chassisui.example.components.Disclosure
import com.chassisui.example.components.EmptyState
import com.chassisui.example.components.InlineBar
import com.chassisui.example.components.ListIcon
import com.chassisui.example.components.ListRow
import com.chassisui.example.components.ListSeparator
import com.chassisui.example.components.SearchField

/** The stack of the Search tab: results, and a push to a detail screen. */
@Composable
fun SearchView() {
    val navController = rememberNavController()
    val transitions = rememberPageTransitions()
    NavHost(
        navController,
        startDestination = "results",
        enterTransition = transitions.enter,
        exitTransition = transitions.exit,
        popEnterTransition = transitions.popEnter,
        popExitTransition = transitions.popExit,
    ) {
        composable("results") {
            SearchScreen(onOpen = { navController.navigate("guide/${it.id}") })
        }
        composable("guide/{id}") { entry ->
            GuideDetailView(guideById(entry.arguments?.getString("id")), navController)
        }
    }
}

/** `Mobile Nav Search Bar` and `Mobile Nav Chip Bar` above results, with an empty state. */
@Composable
private fun SearchScreen(onOpen: (Guide) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(GuideCategory.All) }
    val results = SampleData.guides.filter { guide ->
        (category == GuideCategory.All || guide.category == category) &&
            (query.isEmpty() ||
                guide.title.contains(query, ignoreCase = true) ||
                guide.summary.contains(query, ignoreCase = true))
    }

    Scaffold(
        containerColor = colorResource(R.color.color_context_default_bg_even),
        topBar = { InlineBar("Search") },
    ) { padding ->
        Column(Modifier.padding(padding).consumeWindowInsets(padding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_unit_10)),
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.space_unit_10)),
            ) {
                SearchField(
                    placeholder = "Search guides",
                    text = query,
                    onTextChange = { query = it },
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.space_context_medium)),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall)),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = dimensionResource(R.dimen.space_context_medium)),
                ) {
                    GuideCategory.entries.forEach { option ->
                        Chip(option.title, isSelected = option == category, onClick = { category = option })
                    }
                }
            }
            Box(Modifier.fillMaxSize()) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(results, key = { it.id }) { guide ->
                        ListRow(
                            title = guide.title,
                            subtitle = guide.summary,
                            onClick = { onOpen(guide) },
                            leading = { ListIcon(ChassisIcon.Grid) },
                            trailing = { Disclosure() },
                        )
                        ListSeparator()
                    }
                }
                // Over the list: under it, TalkBack would not reach it
                if (results.isEmpty()) {
                    EmptyState(
                        icon = ChassisIcon.Search,
                        title = "No results",
                        message = "Try another word, or a different filter.",
                    )
                }
            }
        }
    }
}
