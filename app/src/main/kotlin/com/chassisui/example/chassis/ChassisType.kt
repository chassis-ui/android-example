package com.chassisui.example.chassis

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.chassisui.example.R

/**
 * A text style of the tokens, as the names of its resources: a screen width can change the values.
 * The sizes are in `sp`, so the font size of the system scales them.
 */
class ChassisType(
    @param:StringRes val family: Int,
    @param:IntegerRes val weight: Int,
    @param:StringRes val fontStyle: Int,
    @param:DimenRes val size: Int,
    @param:DimenRes val lineHeight: Int,
    @param:DimenRes val letterSpacing: Int,
) {
    /** The font, its line height and its letter spacing, for a `Text`. */
    val style: TextStyle
        @Composable get() {
            val resources = LocalResources.current
            return remember(this, resources, LocalConfiguration.current) { textStyle(resources) }
        }

    private fun textStyle(resources: Resources): TextStyle {
        val fontWeight = FontWeight(resources.getInteger(weight))
        val isItalic = resources.getString(fontStyle) == "italic"
        val slant = if (isItalic) FontStyle.Italic else FontStyle.Normal
        return TextStyle(
            fontFamily = FontFamily(Font(ChassisFonts.fontOf(this, resources), fontWeight, slant)),
            fontWeight = fontWeight,
            fontStyle = slant,
            fontSize = resources.rawValue(size).sp,
            lineHeight = resources.rawValue(lineHeight).sp,
            letterSpacing = resources.rawFloat(letterSpacing).em,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
    }

    /** The text styles this app uses, each read whole from the tokens. */
    companion object {
        val h1 = ChassisType(
            family = R.string.font_context_h1_font_family,
            weight = R.integer.font_context_h1_font_weight,
            fontStyle = R.string.font_context_h1_font_style,
            size = R.dimen.font_context_h1_font_size,
            lineHeight = R.dimen.font_context_h1_line_height,
            letterSpacing = R.dimen.font_context_h1_letter_spacing,
        )

        val h2 = ChassisType(
            family = R.string.font_context_h2_font_family,
            weight = R.integer.font_context_h2_font_weight,
            fontStyle = R.string.font_context_h2_font_style,
            size = R.dimen.font_context_h2_font_size,
            lineHeight = R.dimen.font_context_h2_line_height,
            letterSpacing = R.dimen.font_context_h2_letter_spacing,
        )

        val h3 = ChassisType(
            family = R.string.font_context_h3_font_family,
            weight = R.integer.font_context_h3_font_weight,
            fontStyle = R.string.font_context_h3_font_style,
            size = R.dimen.font_context_h3_font_size,
            lineHeight = R.dimen.font_context_h3_line_height,
            letterSpacing = R.dimen.font_context_h3_letter_spacing,
        )

        val h4 = ChassisType(
            family = R.string.font_context_h4_font_family,
            weight = R.integer.font_context_h4_font_weight,
            fontStyle = R.string.font_context_h4_font_style,
            size = R.dimen.font_context_h4_font_size,
            lineHeight = R.dimen.font_context_h4_line_height,
            letterSpacing = R.dimen.font_context_h4_letter_spacing,
        )

        val h5 = ChassisType(
            family = R.string.font_context_h5_font_family,
            weight = R.integer.font_context_h5_font_weight,
            fontStyle = R.string.font_context_h5_font_style,
            size = R.dimen.font_context_h5_font_size,
            lineHeight = R.dimen.font_context_h5_line_height,
            letterSpacing = R.dimen.font_context_h5_letter_spacing,
        )

        val lead = ChassisType(
            family = R.string.font_context_lead_font_family,
            weight = R.integer.font_context_lead_font_weight,
            fontStyle = R.string.font_context_lead_font_style,
            size = R.dimen.font_context_lead_font_size,
            lineHeight = R.dimen.font_context_lead_line_height,
            letterSpacing = R.dimen.font_context_lead_letter_spacing,
        )

        val titleLarge = ChassisType(
            family = R.string.font_context_title_large_font_family,
            weight = R.integer.font_context_title_large_font_weight,
            fontStyle = R.string.font_context_title_large_font_style,
            size = R.dimen.font_context_title_large_font_size,
            lineHeight = R.dimen.font_context_title_large_line_height,
            letterSpacing = R.dimen.font_context_title_large_letter_spacing,
        )

        val titleMedium = ChassisType(
            family = R.string.font_context_title_medium_font_family,
            weight = R.integer.font_context_title_medium_font_weight,
            fontStyle = R.string.font_context_title_medium_font_style,
            size = R.dimen.font_context_title_medium_font_size,
            lineHeight = R.dimen.font_context_title_medium_line_height,
            letterSpacing = R.dimen.font_context_title_medium_letter_spacing,
        )

        val titleSmall = ChassisType(
            family = R.string.font_context_title_small_font_family,
            weight = R.integer.font_context_title_small_font_weight,
            fontStyle = R.string.font_context_title_small_font_style,
            size = R.dimen.font_context_title_small_font_size,
            lineHeight = R.dimen.font_context_title_small_line_height,
            letterSpacing = R.dimen.font_context_title_small_letter_spacing,
        )

        val bodyLarge = ChassisType(
            family = R.string.font_context_body_large_font_family,
            weight = R.integer.font_context_body_large_font_weight,
            fontStyle = R.string.font_context_body_large_font_style,
            size = R.dimen.font_context_body_large_font_size,
            lineHeight = R.dimen.font_context_body_large_line_height,
            letterSpacing = R.dimen.font_context_body_large_letter_spacing,
        )

        val bodyMedium = ChassisType(
            family = R.string.font_context_body_medium_font_family,
            weight = R.integer.font_context_body_medium_font_weight,
            fontStyle = R.string.font_context_body_medium_font_style,
            size = R.dimen.font_context_body_medium_font_size,
            lineHeight = R.dimen.font_context_body_medium_line_height,
            letterSpacing = R.dimen.font_context_body_medium_letter_spacing,
        )

        val bodySmall = ChassisType(
            family = R.string.font_context_body_small_font_family,
            weight = R.integer.font_context_body_small_font_weight,
            fontStyle = R.string.font_context_body_small_font_style,
            size = R.dimen.font_context_body_small_font_size,
            lineHeight = R.dimen.font_context_body_small_line_height,
            letterSpacing = R.dimen.font_context_body_small_letter_spacing,
        )

        val labelLarge = ChassisType(
            family = R.string.font_context_label_large_font_family,
            weight = R.integer.font_context_label_large_font_weight,
            fontStyle = R.string.font_context_label_large_font_style,
            size = R.dimen.font_context_label_large_font_size,
            lineHeight = R.dimen.font_context_label_large_line_height,
            letterSpacing = R.dimen.font_context_label_large_letter_spacing,
        )

        val labelMedium = ChassisType(
            family = R.string.font_context_label_medium_font_family,
            weight = R.integer.font_context_label_medium_font_weight,
            fontStyle = R.string.font_context_label_medium_font_style,
            size = R.dimen.font_context_label_medium_font_size,
            lineHeight = R.dimen.font_context_label_medium_line_height,
            letterSpacing = R.dimen.font_context_label_medium_letter_spacing,
        )

        val labelSmall = ChassisType(
            family = R.string.font_context_label_small_font_family,
            weight = R.integer.font_context_label_small_font_weight,
            fontStyle = R.string.font_context_label_small_font_style,
            size = R.dimen.font_context_label_small_font_size,
            lineHeight = R.dimen.font_context_label_small_line_height,
            letterSpacing = R.dimen.font_context_label_small_letter_spacing,
        )

        val code = ChassisType(
            family = R.string.font_code_medium_normal_font_family,
            weight = R.integer.font_code_medium_normal_font_weight,
            fontStyle = R.string.font_code_medium_normal_font_style,
            size = R.dimen.font_code_medium_normal_font_size,
            lineHeight = R.dimen.font_code_medium_normal_line_height,
            letterSpacing = R.dimen.font_code_medium_normal_letter_spacing,
        )

        val buttonLarge = ChassisType(
            family = R.string.font_button_large_font_family,
            weight = R.integer.font_button_large_font_weight,
            fontStyle = R.string.font_button_large_font_style,
            size = R.dimen.font_button_large_font_size,
            lineHeight = R.dimen.font_button_large_line_height,
            letterSpacing = R.dimen.font_button_large_letter_spacing,
        )

        val buttonMedium = ChassisType(
            family = R.string.font_button_medium_font_family,
            weight = R.integer.font_button_medium_font_weight,
            fontStyle = R.string.font_button_medium_font_style,
            size = R.dimen.font_button_medium_font_size,
            lineHeight = R.dimen.font_button_medium_line_height,
            letterSpacing = R.dimen.font_button_medium_letter_spacing,
        )

        val buttonSmall = ChassisType(
            family = R.string.font_button_small_font_family,
            weight = R.integer.font_button_small_font_weight,
            fontStyle = R.string.font_button_small_font_style,
            size = R.dimen.font_button_small_font_size,
            lineHeight = R.dimen.font_button_small_line_height,
            letterSpacing = R.dimen.font_button_small_letter_spacing,
        )

        val badge = ChassisType(
            family = R.string.font_badge_small_font_family,
            weight = R.integer.font_badge_small_font_weight,
            fontStyle = R.string.font_badge_small_font_style,
            size = R.dimen.font_badge_small_font_size,
            lineHeight = R.dimen.font_badge_small_line_height,
            letterSpacing = R.dimen.font_badge_small_letter_spacing,
        )

        val chip = ChassisType(
            family = R.string.font_chip_medium_font_family,
            weight = R.integer.font_chip_medium_font_weight,
            fontStyle = R.string.font_chip_medium_font_style,
            size = R.dimen.font_chip_medium_font_size,
            lineHeight = R.dimen.font_chip_medium_line_height,
            letterSpacing = R.dimen.font_chip_medium_letter_spacing,
        )

        val cardTitle = ChassisType(
            family = R.string.font_card_medium_title_font_family,
            weight = R.integer.font_card_medium_title_font_weight,
            fontStyle = R.string.font_card_medium_title_font_style,
            size = R.dimen.font_card_medium_title_font_size,
            lineHeight = R.dimen.font_card_medium_title_line_height,
            letterSpacing = R.dimen.font_card_medium_title_letter_spacing,
        )

        val cardSubtitle = ChassisType(
            family = R.string.font_card_medium_subtitle_font_family,
            weight = R.integer.font_card_medium_subtitle_font_weight,
            fontStyle = R.string.font_card_medium_subtitle_font_style,
            size = R.dimen.font_card_medium_subtitle_font_size,
            lineHeight = R.dimen.font_card_medium_subtitle_line_height,
            letterSpacing = R.dimen.font_card_medium_subtitle_letter_spacing,
        )

        val cardBody = ChassisType(
            family = R.string.font_card_medium_body_font_family,
            weight = R.integer.font_card_medium_body_font_weight,
            fontStyle = R.string.font_card_medium_body_font_style,
            size = R.dimen.font_card_medium_body_font_size,
            lineHeight = R.dimen.font_card_medium_body_line_height,
            letterSpacing = R.dimen.font_card_medium_body_letter_spacing,
        )

        val inputLabel = ChassisType(
            family = R.string.font_form_input_large_label_font_family,
            weight = R.integer.font_form_input_large_label_font_weight,
            fontStyle = R.string.font_form_input_large_label_font_style,
            size = R.dimen.font_form_input_large_label_font_size,
            lineHeight = R.dimen.font_form_input_large_label_line_height,
            letterSpacing = R.dimen.font_form_input_large_label_letter_spacing,
        )

        val inputText = ChassisType(
            family = R.string.font_form_input_large_text_font_family,
            weight = R.integer.font_form_input_large_text_font_weight,
            fontStyle = R.string.font_form_input_large_text_font_style,
            size = R.dimen.font_form_input_large_text_font_size,
            lineHeight = R.dimen.font_form_input_large_text_line_height,
            letterSpacing = R.dimen.font_form_input_large_text_letter_spacing,
        )

        val assist = ChassisType(
            family = R.string.font_assist_medium_font_family,
            weight = R.integer.font_assist_medium_font_weight,
            fontStyle = R.string.font_assist_medium_font_style,
            size = R.dimen.font_assist_medium_font_size,
            lineHeight = R.dimen.font_assist_medium_line_height,
            letterSpacing = R.dimen.font_assist_medium_letter_spacing,
        )

        val notificationTitle = ChassisType(
            family = R.string.font_notification_title_font_family,
            weight = R.integer.font_notification_title_font_weight,
            fontStyle = R.string.font_notification_title_font_style,
            size = R.dimen.font_notification_title_font_size,
            lineHeight = R.dimen.font_notification_title_line_height,
            letterSpacing = R.dimen.font_notification_title_letter_spacing,
        )

        val notificationMessage = ChassisType(
            family = R.string.font_notification_message_font_family,
            weight = R.integer.font_notification_message_font_weight,
            fontStyle = R.string.font_notification_message_font_style,
            size = R.dimen.font_notification_message_font_size,
            lineHeight = R.dimen.font_notification_message_line_height,
            letterSpacing = R.dimen.font_notification_message_letter_spacing,
        )

        val sectionHeader = ChassisType(
            family = R.string.font_section_header_medium_font_family,
            weight = R.integer.font_section_header_medium_font_weight,
            fontStyle = R.string.font_section_header_medium_font_style,
            size = R.dimen.font_section_header_medium_font_size,
            lineHeight = R.dimen.font_section_header_medium_line_height,
            letterSpacing = R.dimen.font_section_header_medium_letter_spacing,
        )

        val navTitle = ChassisType(
            family = R.string.font_ui_medium_strong_font_family,
            weight = R.integer.font_ui_medium_strong_font_weight,
            fontStyle = R.string.font_ui_medium_strong_font_style,
            size = R.dimen.font_ui_medium_strong_font_size,
            lineHeight = R.dimen.font_ui_medium_strong_line_height,
            letterSpacing = R.dimen.font_ui_medium_strong_letter_spacing,
        )

        val segment = ChassisType(
            family = R.string.font_ui_small_strong_font_family,
            weight = R.integer.font_ui_small_strong_font_weight,
            fontStyle = R.string.font_ui_small_strong_font_style,
            size = R.dimen.font_ui_small_strong_font_size,
            lineHeight = R.dimen.font_ui_small_strong_line_height,
            letterSpacing = R.dimen.font_ui_small_strong_letter_spacing,
        )

        val tab = ChassisType(
            family = R.string.font_ui_2xsmall_normal_font_family,
            weight = R.integer.font_ui_2xsmall_normal_font_weight,
            fontStyle = R.string.font_ui_2xsmall_normal_font_style,
            size = R.dimen.font_ui_2xsmall_normal_font_size,
            lineHeight = R.dimen.font_ui_2xsmall_normal_line_height,
            letterSpacing = R.dimen.font_ui_2xsmall_normal_letter_spacing,
        )

        /** Every style, by its name: to resolve at launch and to show in Foundations. */
        val all = listOf(
            "h1" to h1,
            "h2" to h2,
            "h3" to h3,
            "h4" to h4,
            "h5" to h5,
            "lead" to lead,
            "titleLarge" to titleLarge,
            "titleMedium" to titleMedium,
            "titleSmall" to titleSmall,
            "bodyLarge" to bodyLarge,
            "bodyMedium" to bodyMedium,
            "bodySmall" to bodySmall,
            "labelLarge" to labelLarge,
            "labelMedium" to labelMedium,
            "labelSmall" to labelSmall,
            "code" to code,
            "buttonLarge" to buttonLarge,
            "buttonMedium" to buttonMedium,
            "buttonSmall" to buttonSmall,
            "badge" to badge,
            "chip" to chip,
            "cardTitle" to cardTitle,
            "cardSubtitle" to cardSubtitle,
            "cardBody" to cardBody,
            "inputLabel" to inputLabel,
            "inputText" to inputText,
            "assist" to assist,
            "notificationTitle" to notificationTitle,
            "notificationMessage" to notificationMessage,
            "sectionHeader" to sectionHeader,
            "navTitle" to navTitle,
            "segment" to segment,
            "tab" to tab,
        )
    }
}

/** The number of a dimension as it is written. `getDimension` would scale an `sp` a second time. */
fun Resources.rawValue(@DimenRes id: Int): Float {
    val value = TypedValue()
    getValue(id, value, true)
    return TypedValue.complexToFloat(value.data)
}

/** A token that is a plain number, such as an opacity or a part of an easing. */
fun Resources.rawFloat(@DimenRes id: Int): Float {
    val value = TypedValue()
    getValue(id, value, true)
    return value.float
}
