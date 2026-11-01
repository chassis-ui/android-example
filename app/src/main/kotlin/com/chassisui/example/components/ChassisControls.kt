package com.chassisui.example.components

import androidx.annotation.ColorRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.chassisui.example.R
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisMotion
import com.chassisui.example.chassis.ChassisShadow
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.Icon
import com.chassisui.example.chassis.chassisShadow
import com.chassisui.example.chassis.rawValue

enum class ButtonKind { Primary, Outline, Danger }

private class ButtonColors(
    @param:ColorRes val foreground: Int,
    @param:ColorRes val background: Int,
    @param:ColorRes val border: Int,
)

/** `Mobile Button` of the Figma library. The tokens have a color for each state of each kind. */
@Composable
fun ChassisButton(
    title: String,
    modifier: Modifier = Modifier,
    kind: ButtonKind = ButtonKind.Primary,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val colors = buttonColors(kind, isPressed, enabled)
    val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_button_large))
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .heightIn(min = dimensionResource(R.dimen.size_button_large_main))
            .background(colorResource(colors.background), shape)
            .border(dimensionResource(R.dimen.border_width_context_xlarge), colorResource(colors.border), shape)
            .clickable(interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(
                horizontal = dimensionResource(R.dimen.space_button_large_padding_x),
                vertical = dimensionResource(R.dimen.space_button_large_padding_y),
            ),
    ) {
        Text(
            title,
            style = ChassisType.buttonLarge.style,
            color = colorResource(colors.foreground),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun buttonColors(kind: ButtonKind, isPressed: Boolean, enabled: Boolean): ButtonColors = when (kind) {
    ButtonKind.Primary -> when {
        !enabled -> ButtonColors(
            R.color.color_button_primary_fg_disabled,
            R.color.color_button_primary_bg_disabled,
            R.color.color_button_primary_border_disabled,
        )
        isPressed -> ButtonColors(
            R.color.color_button_primary_fg_press,
            R.color.color_button_primary_bg_press,
            R.color.color_button_primary_border_press,
        )
        else -> ButtonColors(
            R.color.color_button_primary_fg_idle,
            R.color.color_button_primary_bg_idle,
            R.color.color_button_primary_border_idle,
        )
    }
    // The secondary button of `Action Bar`: the fill color of the primary as its outline
    ButtonKind.Outline -> {
        val line = when {
            !enabled -> R.color.color_button_primary_bg_disabled
            isPressed -> R.color.color_button_primary_bg_press
            else -> R.color.color_button_primary_bg_idle
        }
        ButtonColors(line, R.color.color_context_default_transparent_color, line)
    }
    ButtonKind.Danger ->
        if (isPressed) {
            ButtonColors(
                R.color.color_button_danger_fg_press,
                R.color.color_button_danger_bg_press,
                R.color.color_button_danger_border_press,
            )
        } else {
            ButtonColors(
                R.color.color_button_danger_fg_idle,
                R.color.color_button_danger_bg_idle,
                R.color.color_button_danger_border_idle,
            )
        }
}

/** `Action Bar` of `Mobile Bottom Navigation`: buttons above the system bar, side by side. */
@Composable
fun ActionBar(content: @Composable RowScope.() -> Unit) {
    Column(Modifier.background(colorResource(R.color.color_context_default_bg_even))) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.size_asset_separator))
                .background(colorResource(R.color.color_context_default_border_subtle)),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_medium)),
            modifier = Modifier
                .navigationBarsPadding()
                .padding(dimensionResource(R.dimen.space_context_medium)),
            content = content,
        )
    }
}

/** `Mobile Nav Segment Bar`. Material has no segmented control that takes these tokens whole. */
@Composable
fun <T> SegmentBar(
    options: List<Pair<T, String>>,
    selection: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var width by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val partWidth = with(density) { (width / options.size.coerceAtLeast(1)).toDp() }
    val offset by animateDpAsState(
        targetValue = partWidth * options.indexOfFirst { it.first == selection },
        animationSpec = ChassisMotion.standard(),
        label = "segment",
    )
    Box(
        modifier
            .fillMaxWidth()
            .background(colorResource(R.color.color_context_default_dim_slight), CircleShape)
            .padding(dimensionResource(R.dimen.space_unit_2))
            .onSizeChanged { width = it.width },
    ) {
        Box(Modifier.matchParentSize()) {
            Box(
                Modifier
                    .offset { IntOffset(offset.roundToPx(), 0) }
                    .width(partWidth)
                    .fillMaxHeight()
                    .chassisShadow(ChassisShadow.Small, CircleShape)
                    .background(colorResource(R.color.color_context_default_bg_main), CircleShape),
            )
        }
        Row {
            options.forEach { (value, title) ->
                val minimumSize = LocalResources.current.rawValue(R.dimen.typography_font_size_ui_2xsmall).sp
                Text(
                    title,
                    style = ChassisType.segment.style,
                    color = colorResource(R.color.color_context_default_fg_main),
                    maxLines = 1,
                    // One line in equal parts: a larger size shrinks to fit rather than cut a label short
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = minimumSize,
                        maxFontSize = ChassisType.segment.style.fontSize,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = value == selection,
                            interactionSource = null,
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelect(value) },
                        )
                        .padding(
                            horizontal = dimensionResource(R.dimen.space_unit_12),
                            vertical = dimensionResource(R.dimen.space_unit_6),
                        ),
                )
            }
        }
    }
}

/** `_ Search - Basic` of `Mobile Nav Search Bar`. */
@Composable
fun SearchField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtle = colorResource(R.color.color_context_default_fg_subtle)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_unit_8)),
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.color_context_default_dim_slight), CircleShape)
            .padding(dimensionResource(R.dimen.space_unit_12)),
    ) {
        Icon(ChassisIcon.Search, dimensionResource(R.dimen.size_icon_glyph_small), tint = subtle)
        PlainField(
            value = text,
            onValueChange = onTextChange,
            style = ChassisType.bodySmall.style,
            color = colorResource(R.color.color_context_default_fg_main),
            caret = colorResource(R.color.color_context_primary_base_color),
            placeholder = placeholder,
            placeholderColor = subtle,
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = placeholder },
        )
        if (text.isNotEmpty()) {
            Box(
                Modifier
                    .clickable(role = Role.Button, onClickLabel = "Clear") { onTextChange("") }
                    .semantics { contentDescription = "Clear" },
            ) {
                Icon(ChassisIcon.Close, dimensionResource(R.dimen.size_icon_glyph_small), tint = subtle)
            }
        }
    }
}

/** A text field with no frame of its own, in a style of the tokens. */
@Composable
fun PlainField(
    value: String,
    onValueChange: (String) -> Unit,
    style: TextStyle,
    color: Color,
    caret: Color,
    placeholder: String,
    placeholderColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = style.copy(color = color),
        cursorBrush = SolidColor(caret),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        modifier = modifier,
        decorationBox = { field ->
            Box {
                if (value.isEmpty()) {
                    Text(placeholder, style = style, color = placeholderColor, maxLines = 1)
                }
                field()
            }
        },
    )
}

enum class FieldStatus { Regular, Error, Success }

private class FieldColors(
    @param:ColorRes val background: Int,
    @param:ColorRes val border: Int,
    @param:ColorRes val foregroundActive: Int,
    @param:ColorRes val foregroundInactive: Int,
    @param:ColorRes val caret: Int,
    @param:ColorRes val help: Int,
)

/** A text input of the form tokens, with a color for each of its states. */
@Composable
fun ChassisTextField(
    label: String,
    icon: ChassisIcon,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    help: String = "",
    status: FieldStatus = FieldStatus.Regular,
    isSecure: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    autofill: ContentType? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val isFocused by interaction.collectIsFocusedAsState()
    var isRevealed by rememberSaveable { mutableStateOf(false) }
    val colors = fieldColors(status, isFocused, enabled)
    val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_form_input_large))
    val inactive = colorResource(colors.foregroundInactive)
    val iconSize = dimensionResource(R.dimen.size_form_input_large_icon)

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_2xsmall)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            label,
            style = ChassisType.inputLabel.style,
            color = colorResource(R.color.color_context_default_fg_main),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_form_input_large_gap)),
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(colors.background), shape)
                .border(dimensionResource(R.dimen.border_width_context_medium), colorResource(colors.border), shape)
                .padding(
                    horizontal = dimensionResource(R.dimen.space_form_input_large_padding_x),
                    vertical = dimensionResource(R.dimen.space_form_input_large_padding_y),
                ),
        ) {
            Icon(icon, iconSize, tint = inactive)
            PlainField(
                value = text,
                onValueChange = onTextChange,
                style = ChassisType.inputText.style,
                color = colorResource(colors.foregroundActive),
                caret = colorResource(colors.caret),
                placeholder = placeholder,
                placeholderColor = inactive,
                enabled = enabled,
                keyboardOptions = keyboardOptions,
                visualTransformation = if (isSecure && !isRevealed) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                interactionSource = interaction,
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = label
                        if (autofill != null) contentType = autofill
                    },
            )
            if (isSecure) {
                val revealLabel = if (isRevealed) "Hide password" else "Show password"
                Box(
                    Modifier
                        .clickable(enabled = enabled, role = Role.Button) { isRevealed = !isRevealed }
                        .semantics { contentDescription = revealLabel },
                ) {
                    Icon(if (isRevealed) ChassisIcon.EyeOff else ChassisIcon.Eye, iconSize, tint = inactive)
                }
            }
        }
        if (help.isNotEmpty()) {
            Text(help, style = ChassisType.assist.style, color = colorResource(colors.help))
        }
    }
}

private fun fieldColors(status: FieldStatus, isFocused: Boolean, enabled: Boolean): FieldColors = when {
    !enabled -> FieldColors(
        R.color.color_form_input_disabled_bg_regular,
        R.color.color_form_input_disabled_border,
        R.color.color_form_input_disabled_fg_active,
        R.color.color_form_input_disabled_fg_inactive,
        R.color.color_form_input_disabled_caret,
        R.color.color_form_input_disabled_help,
    )
    status == FieldStatus.Error -> FieldColors(
        R.color.color_form_input_error_bg_regular,
        R.color.color_form_input_error_border,
        R.color.color_form_input_error_fg_active,
        R.color.color_form_input_error_fg_inactive,
        R.color.color_form_input_error_caret,
        R.color.color_form_input_error_help,
    )
    status == FieldStatus.Success -> FieldColors(
        R.color.color_form_input_success_bg_regular,
        R.color.color_form_input_success_border,
        R.color.color_form_input_success_fg_active,
        R.color.color_form_input_success_fg_inactive,
        R.color.color_form_input_success_caret,
        R.color.color_form_input_success_help,
    )
    isFocused -> FieldColors(
        R.color.color_form_input_focus_bg_regular,
        R.color.color_form_input_focus_border,
        R.color.color_form_input_focus_fg_active,
        R.color.color_form_input_focus_fg_inactive,
        R.color.color_form_input_focus_caret,
        R.color.color_form_input_focus_help,
    )
    else -> FieldColors(
        R.color.color_form_input_idle_bg_regular,
        R.color.color_form_input_idle_border,
        R.color.color_form_input_idle_fg_active,
        R.color.color_form_input_idle_fg_inactive,
        R.color.color_form_input_idle_caret,
        R.color.color_form_input_idle_help,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun barColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = colorResource(R.color.color_context_default_bg_even),
    scrolledContainerColor = colorResource(R.color.color_context_default_bg_even),
    titleContentColor = colorResource(R.color.color_context_default_fg_main),
    navigationIconContentColor = colorResource(R.color.color_context_default_icon_main),
    actionIconContentColor = colorResource(R.color.color_context_default_icon_main),
)

/** `Mobile Nav Control Bar` with a large title, for a tab. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable RowScope.() -> Unit = {},
) {
    LargeTopAppBar(
        title = { Text(title) },
        actions = actions,
        colors = barColors(),
        scrollBehavior = scrollBehavior,
    )
}

/** `Mobile Nav Control Bar` with an inline title, for a pushed screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if (onBack != null) {
                BarButton(ChassisIcon.Back, "Back", onBack)
            }
        },
        actions = actions,
        colors = barColors(),
    )
}

/** A button of a bar: an icon with a label for TalkBack. */
@Composable
fun BarButton(icon: ChassisIcon, label: String, onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.semantics { contentDescription = label },
    ) {
        Icon(icon, tint = LocalContentColor.current)
    }
}
