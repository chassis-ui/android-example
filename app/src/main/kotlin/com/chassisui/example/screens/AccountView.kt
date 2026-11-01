package com.chassisui.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import com.chassisui.example.AppearanceChoice
import com.chassisui.example.LocalAppModel
import com.chassisui.example.Plan
import com.chassisui.example.R
import com.chassisui.example.SampleData
import com.chassisui.example.Toast
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.Icon
import com.chassisui.example.components.ActionBar
import com.chassisui.example.components.Avatar
import com.chassisui.example.components.BarButton
import com.chassisui.example.components.ChassisButton
import com.chassisui.example.components.Disclosure
import com.chassisui.example.components.InlineBar
import com.chassisui.example.components.LargeBar
import com.chassisui.example.components.ListIcon
import com.chassisui.example.components.ListRow
import com.chassisui.example.components.ListSeparator
import com.chassisui.example.components.SectionTitle
import com.chassisui.example.components.SegmentBar
import kotlinx.coroutines.launch

/** Settings: a profile, a toggle, a sheet, a choice of appearance and a confirmation. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView() {
    val model = LocalAppModel.current
    var isPricingShown by rememberSaveable { mutableStateOf(false) }
    var isSignOutShown by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val name = "Jordan Lee"

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colorResource(R.color.color_context_default_bg_even),
        topBar = { LargeBar("Account", scrollBehavior) },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_large)),
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.space_context_medium)),
        ) {
            ListSection {
                ListRow(
                    title = name,
                    subtitle = "${model.plan.title} plan",
                    leading = { Avatar(name, dimensionResource(R.dimen.size_context_large)) },
                )
            }

            ListSection("Preferences") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall)),
                    modifier = Modifier
                        .background(colorResource(R.color.color_context_default_bg_main))
                        .padding(
                            horizontal = dimensionResource(R.dimen.space_list_padding_x),
                            vertical = dimensionResource(R.dimen.space_list_padding_y),
                        ),
                ) {
                    Text(
                        "Appearance",
                        style = ChassisType.labelLarge.style,
                        color = colorResource(R.color.color_context_default_fg_main),
                    )
                    SegmentBar(
                        options = AppearanceChoice.entries.map { it to it.name },
                        selection = model.appearance,
                        onSelect = model::updateAppearance,
                    )
                }
                ListSeparator()
                ListRow(
                    title = "Notifications",
                    leading = { ListIcon(ChassisIcon.Bell) },
                    trailing = {
                        Switch(
                            checked = model.notificationsOn,
                            onCheckedChange = model::updateNotificationsOn,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = colorResource(R.color.color_context_primary_base_color),
                                checkedThumbColor = colorResource(R.color.color_context_primary_contrast_color),
                                checkedBorderColor = colorResource(R.color.color_context_primary_base_color),
                                uncheckedTrackColor = colorResource(R.color.color_context_default_dim_slight),
                                uncheckedThumbColor = colorResource(R.color.color_context_default_fg_subtle),
                                uncheckedBorderColor = colorResource(R.color.color_context_default_border_subtle),
                            ),
                        )
                    },
                )
            }

            ListSection("Account") {
                ListRow(
                    title = "Plan",
                    subtitle = model.plan.price,
                    onClick = { isPricingShown = true },
                    leading = { ListIcon(ChassisIcon.Star) },
                    trailing = { Disclosure() },
                )
                ListSeparator()
                ListRow(
                    title = "Sign out",
                    onClick = { isSignOutShown = true },
                    leading = { ListIcon(ChassisIcon.SignOut) },
                )
            }
        }
    }

    if (isPricingShown) {
        PricingSheet(onDismiss = { isPricingShown = false })
    }

    if (isSignOutShown) {
        AlertDialog(
            onDismissRequest = { isSignOutShown = false },
            title = { Text("Sign out?") },
            containerColor = colorResource(R.color.color_context_default_bg_main),
            titleContentColor = colorResource(R.color.color_context_default_fg_main),
            confirmButton = {
                TextButton(onClick = {
                    isSignOutShown = false
                    model.updateSignedIn(false)
                }) {
                    Text("Sign out", color = colorResource(R.color.color_context_danger_fg_main))
                }
            },
            dismissButton = {
                TextButton(onClick = { isSignOutShown = false }) {
                    Text("Cancel", color = colorResource(R.color.color_context_default_fg_main))
                }
            },
        )
    }
}

/** A section of the inset list, with a title in the section tokens. */
@Composable
private fun ListSection(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall))) {
        if (title != null) {
            SectionTitle(title, Modifier.padding(horizontal = dimensionResource(R.dimen.space_list_padding_x)))
        }
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensionResource(R.dimen.border_radius_card_main))),
            content = content,
        )
    }
}

/** A sheet as high as its content, the plans to pick from and an `Action Bar` that stays in view. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PricingSheet(onDismiss: () -> Unit) {
    val model = LocalAppModel.current
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedId by rememberSaveable { mutableStateOf(model.plan.id) }
    val selected = SampleData.plans.first { it.id == selectedId }
    val radius = dimensionResource(R.dimen.border_radius_modal_main)

    fun close() {
        scope.launch { state.hide() }.invokeOnCompletion { onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        shape = RoundedCornerShape(topStart = radius, topEnd = radius),
        containerColor = colorResource(R.color.color_context_default_bg_main),
        scrimColor = colorResource(R.color.color_context_default_dim_subtle),
        dragHandle = null,
    ) {
        InlineBar("Choose a plan") {
            BarButton(ChassisIcon.Close, "Close", { close() })
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_modal_medium_gap)),
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.space_modal_medium_padding_x)),
        ) {
            SampleData.plans.forEach { plan ->
                PlanCard(plan, isSelected = plan.id == selectedId, onClick = { selectedId = plan.id })
            }
        }
        ActionBar {
            ChassisButton("Choose ${selected.title}", Modifier.weight(1f)) {
                model.plan = selected
                model.show(Toast(title = "Plan changed", message = "You are on the ${selected.title} plan."))
                close()
            }
        }
    }
}

@Composable
private fun PlanCard(plan: Plan, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_card_main))
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall)),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorResource(
                    if (isSelected) R.color.color_context_primary_bg_even else R.color.color_context_default_bg_main,
                ),
                shape,
            )
            .border(
                dimensionResource(
                    if (isSelected) R.dimen.border_width_context_xlarge else R.dimen.border_width_context_medium,
                ),
                colorResource(
                    if (isSelected) R.color.color_context_primary_border_main else R.color.color_context_default_border_subtle,
                ),
                shape,
            )
            .clip(shape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(dimensionResource(R.dimen.space_context_medium))
            .semantics(mergeDescendants = true) {},
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                plan.title,
                style = ChassisType.cardTitle.style,
                color = colorResource(R.color.color_context_default_fg_main),
            )
            Text(
                plan.price,
                style = ChassisType.cardSubtitle.style,
                color = colorResource(R.color.color_context_default_fg_subtle),
            )
        }
        plan.features.forEach { feature ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall)),
            ) {
                Icon(
                    ChassisIcon.Check,
                    dimensionResource(R.dimen.size_icon_glyph_xsmall),
                    tint = colorResource(R.color.color_context_success_icon_main),
                )
                Text(
                    feature,
                    style = ChassisType.cardBody.style,
                    color = colorResource(R.color.color_context_default_fg_main),
                )
            }
        }
    }
}
