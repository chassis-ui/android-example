package com.chassisui.example.screens

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chassisui.example.GuideCategory
import com.chassisui.example.R
import com.chassisui.example.Toast
import com.chassisui.example.chassis.ChassisEasing
import com.chassisui.example.chassis.ChassisFonts
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisMotion
import com.chassisui.example.chassis.ChassisShadow
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.Icon
import com.chassisui.example.chassis.chassisShadow
import com.chassisui.example.chassis.rememberPageTransitions
import com.chassisui.example.components.Avatar
import com.chassisui.example.components.Badge
import com.chassisui.example.components.ButtonKind
import com.chassisui.example.components.ChassisButton
import com.chassisui.example.components.ChassisTextField
import com.chassisui.example.components.Chip
import com.chassisui.example.components.Disclosure
import com.chassisui.example.components.FieldStatus
import com.chassisui.example.components.InlineBar
import com.chassisui.example.components.LargeBar
import com.chassisui.example.components.ListIcon
import com.chassisui.example.components.ListRow
import com.chassisui.example.components.ListSeparator
import com.chassisui.example.components.NotificationBanner
import com.chassisui.example.components.SectionTitle
import com.chassisui.example.components.SegmentBar
import com.chassisui.example.components.Skeleton

private enum class Topic(val icon: ChassisIcon) {
    Colors(ChassisIcon.Palette),
    Typography(ChassisIcon.Sparkles),
    Spacing(ChassisIcon.Grid),
    Radius(ChassisIcon.Cube),
    Shadows(ChassisIcon.Moon),
    Motion(ChassisIcon.Globe),
    Icons(ChassisIcon.Star),
    Components(ChassisIcon.Filter),
}

/** The tokens and assets of the brand of this build, to look at and compare between brands. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundationsView() {
    val navController = rememberNavController()
    val transitions = rememberPageTransitions()
    NavHost(
        navController,
        startDestination = "topics",
        enterTransition = transitions.enter,
        exitTransition = transitions.exit,
        popEnterTransition = transitions.popEnter,
        popExitTransition = transitions.popExit,
    ) {
        composable("topics") {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                containerColor = colorResource(R.color.color_context_default_bg_even),
                topBar = { LargeBar("Foundations", scrollBehavior) },
            ) { padding ->
                LazyColumn(Modifier.padding(padding).consumeWindowInsets(padding).fillMaxSize()) {
                    items(Topic.entries) { topic ->
                        ListRow(
                            title = topic.name,
                            onClick = { navController.navigate(topic.name) },
                            leading = { ListIcon(topic.icon) },
                            trailing = { Disclosure() },
                        )
                        ListSeparator()
                    }
                }
            }
        }
        Topic.entries.forEach { topic ->
            composable(topic.name) {
                Scaffold(
                    containerColor = colorResource(R.color.color_context_default_bg_even),
                    topBar = { InlineBar(topic.name, onBack = { navController.popBackStack() }) },
                ) { padding ->
                    Box(Modifier.padding(padding).consumeWindowInsets(padding)) {
                        when (topic) {
                            Topic.Colors -> ColorsPage()
                            Topic.Typography -> TypographyPage()
                            Topic.Spacing -> SpacingPage()
                            Topic.Radius -> RadiusPage()
                            Topic.Shadows -> ShadowsPage()
                            Topic.Motion -> MotionPage()
                            Topic.Icons -> IconsPage()
                            Topic.Components -> ComponentsPage()
                        }
                    }
                }
            }
        }
    }
}

/** A name in the code font, as the token is written. */
@Composable
private fun TokenName(name: String, modifier: Modifier = Modifier) {
    Text(
        name,
        style = ChassisType.code.style,
        color = colorResource(R.color.color_context_default_fg_subtle),
        modifier = modifier,
    )
}

@Composable
private fun Page(content: @Composable ColumnScope.() -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_medium)),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.space_context_medium)),
        content = content,
    )
}

private class ColorGroup(val name: String, val swatches: List<Pair<String, Int>>)

private val colorGroups = listOf(
    ColorGroup(
        "Primary",
        listOf(
            "base_color" to R.color.color_context_primary_base_color,
            "bg_main" to R.color.color_context_primary_bg_main,
            "bg_even" to R.color.color_context_primary_bg_even,
            "fg_main" to R.color.color_context_primary_fg_main,
        ),
    ),
    ColorGroup(
        "Secondary",
        listOf(
            "base_color" to R.color.color_context_secondary_base_color,
            "bg_main" to R.color.color_context_secondary_bg_main,
            "bg_even" to R.color.color_context_secondary_bg_even,
            "fg_main" to R.color.color_context_secondary_fg_main,
        ),
    ),
    ColorGroup(
        "Default",
        listOf(
            "bg_main" to R.color.color_context_default_bg_main,
            "bg_even" to R.color.color_context_default_bg_even,
            "fg_main" to R.color.color_context_default_fg_main,
            "fg_subtle" to R.color.color_context_default_fg_subtle,
        ),
    ),
    ColorGroup(
        "Success",
        listOf(
            "base_color" to R.color.color_context_success_base_color,
            "bg_main" to R.color.color_context_success_bg_main,
            "bg_even" to R.color.color_context_success_bg_even,
            "fg_main" to R.color.color_context_success_fg_main,
        ),
    ),
    ColorGroup(
        "Warning",
        listOf(
            "base_color" to R.color.color_context_warning_base_color,
            "bg_main" to R.color.color_context_warning_bg_main,
            "bg_even" to R.color.color_context_warning_bg_even,
            "fg_main" to R.color.color_context_warning_fg_main,
        ),
    ),
    ColorGroup(
        "Danger",
        listOf(
            "base_color" to R.color.color_context_danger_base_color,
            "bg_main" to R.color.color_context_danger_bg_main,
            "bg_even" to R.color.color_context_danger_bg_even,
            "fg_main" to R.color.color_context_danger_fg_main,
        ),
    ),
)

@Composable
private fun ColorsPage() {
    Page {
        colorGroups.forEach { group ->
            SectionTitle(group.name)
            group.swatches.forEach { (name, @ColorRes color) ->
                val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_context_medium))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_small)),
                ) {
                    Box(
                        Modifier
                            .size(dimensionResource(R.dimen.size_context_medium))
                            .background(colorResource(color), shape)
                            .border(
                                dimensionResource(R.dimen.border_width_context_medium),
                                colorResource(R.color.color_context_default_border_subtle),
                                shape,
                            ),
                    )
                    TokenName(name)
                }
            }
        }
    }
}

@Composable
private fun TypographyPage() {
    val resources = LocalResources.current
    Page {
        ChassisType.all.forEach { (name, type) ->
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_unit_2))) {
                Text(
                    stringResource(type.family),
                    style = type.style,
                    color = colorResource(R.color.color_context_default_fg_main),
                )
                TokenName("$name · ${ChassisFonts.fileName(type, resources)}")
            }
        }
    }
}

@Composable
private fun SpacingPage() {
    val steps = listOf(
        "2xsmall" to R.dimen.space_context_2xsmall,
        "xsmall" to R.dimen.space_context_xsmall,
        "small" to R.dimen.space_context_small,
        "medium" to R.dimen.space_context_medium,
        "large" to R.dimen.space_context_large,
        "xlarge" to R.dimen.space_context_xlarge,
        "2xlarge" to R.dimen.space_context_2xlarge,
    )
    Page {
        steps.forEach { (name, @DimenRes space) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_small)),
            ) {
                Box(
                    Modifier
                        .width(dimensionResource(space))
                        .height(dimensionResource(R.dimen.size_context_xsmall))
                        .background(colorResource(R.color.color_context_primary_base_color)),
                )
                TokenName("space_context_$name")
            }
        }
    }
}

@Composable
private fun RadiusPage() {
    val steps = listOf(
        "small" to R.dimen.border_radius_context_small,
        "medium" to R.dimen.border_radius_context_medium,
        "large" to R.dimen.border_radius_context_large,
        "xlarge" to R.dimen.border_radius_context_xlarge,
        "2xlarge" to R.dimen.border_radius_context_2xlarge,
        "button" to R.dimen.border_radius_button_large,
        "card" to R.dimen.border_radius_card_main,
    )
    Page {
        steps.forEach { (name, @DimenRes radius) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_small)),
            ) {
                Box(
                    Modifier
                        .size(dimensionResource(R.dimen.size_context_xlarge))
                        .background(
                            colorResource(R.color.color_context_primary_bg_evident),
                            RoundedCornerShape(dimensionResource(radius)),
                        ),
                )
                TokenName(name)
            }
        }
    }
}

@Composable
private fun ShadowsPage() {
    val shadows = listOf(
        "shadow_context_small" to ChassisShadow.Small,
        "shadow_context_medium" to ChassisShadow.Medium,
        "shadow_context_large" to ChassisShadow.Large,
        "shadow_card_main" to ChassisShadow.Card,
    )
    val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_card_main))
    Page {
        shadows.forEach { (name, shadow) ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .chassisShadow(shadow, shape)
                    .background(colorResource(R.color.color_context_default_bg_main), shape)
                    .padding(dimensionResource(R.dimen.space_context_large)),
            ) {
                TokenName(name)
            }
        }
    }
}

@Composable
private fun MotionPage() {
    var isMoved by rememberSaveable { mutableStateOf(false) }
    val timings = listOf(
        "small" to R.integer.motion_timing_context_small,
        "medium" to R.integer.motion_timing_context_medium,
        "xlarge" to R.integer.motion_timing_context_xlarge,
        "3xlarge" to R.integer.motion_timing_context_3xlarge,
    )
    val dot = dimensionResource(R.dimen.size_context_xsmall)
    Page {
        timings.forEach { (name, @IntegerRes timing) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_unit_4)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                TokenName("motion_timing_context_$name")
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val offset by animateDpAsState(
                        targetValue = if (isMoved) maxWidth - dot else dimensionResource(R.dimen.space_unit_0),
                        animationSpec = ChassisMotion.curve(ChassisEasing.Standard, timing),
                        label = name,
                    )
                    Box(
                        Modifier
                            .offset { IntOffset(offset.roundToPx(), 0) }
                            .size(dot)
                            .background(colorResource(R.color.color_context_primary_base_color), CircleShape),
                    )
                }
            }
        }
        ChassisButton("Play", Modifier.fillMaxWidth(), onClick = { isMoved = !isMoved })
    }
}

@Composable
private fun IconsPage() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(dimensionResource(R.dimen.size_unit_80)),
        contentPadding = PaddingValues(dimensionResource(R.dimen.space_context_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_small)),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(ChassisIcon.entries) { icon ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_unit_4)),
            ) {
                Icon(icon, tint = colorResource(R.color.color_context_default_icon_main))
                Text(
                    icon.name,
                    style = ChassisType.labelSmall.style,
                    color = colorResource(R.color.color_context_default_fg_subtle),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ComponentsPage() {
    var text by rememberSaveable { mutableStateOf("") }
    var segment by rememberSaveable { mutableStateOf(GuideCategory.All) }
    var isChipOn by rememberSaveable { mutableStateOf(true) }
    Page {
        SectionTitle("Buttons")
        ChassisButton("Primary", Modifier.fillMaxWidth(), onClick = {})
        ChassisButton("Outline", Modifier.fillMaxWidth(), kind = ButtonKind.Outline, onClick = {})
        ChassisButton("Danger", Modifier.fillMaxWidth(), kind = ButtonKind.Danger, onClick = {})
        ChassisButton("Disabled", Modifier.fillMaxWidth(), enabled = false, onClick = {})

        SectionTitle("Selection")
        SegmentBar(
            options = GuideCategory.entries.map { it to it.title },
            selection = segment,
            onSelect = { segment = it },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall))) {
            Chip("Selected", isSelected = isChipOn, onClick = { isChipOn = !isChipOn })
            Chip("Not selected", isSelected = !isChipOn, onClick = { isChipOn = !isChipOn })
        }

        SectionTitle("Fields")
        ChassisTextField("Idle", ChassisIcon.Email, text, { text = it }, placeholder = "Placeholder")
        ChassisTextField("Error", ChassisIcon.Email, text, { text = it }, help = "What went wrong", status = FieldStatus.Error)
        ChassisTextField("Success", ChassisIcon.Check, text, { text = it }, help = "All good", status = FieldStatus.Success)
        ChassisTextField("Disabled", ChassisIcon.Lock, text, { text = it }, placeholder = "Not now", enabled = false)

        SectionTitle("Status")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_small)),
        ) {
            Avatar("Maya Chen")
            Badge(GuideCategory.entries.size.toString())
            CircularProgressIndicator(
                color = colorResource(R.color.color_context_primary_base_color),
                modifier = Modifier.size(dimensionResource(R.dimen.size_icon_glyph_medium)),
            )
        }
        Skeleton()
        NotificationBanner(remember { Toast(title = "Done", message = "This is a notification.") })
        NotificationBanner(remember { Toast(title = "Failed", message = "This one is an error.", isError = true) })
    }
}
