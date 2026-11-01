package com.chassisui.example.chassis

import android.content.res.Resources
import androidx.annotation.FontRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import com.chassisui.example.R

/**
 * The font files of the brand, named by role and weight: `ui_normal.ttf`, `display_mass.ttf`.
 * Which family a role holds is the choice of the brand, so no family name is in this app.
 */
object ChassisFonts {
    /** Resolves every style at launch: a style without a file stops the app here, not on a screen. */
    fun check(resources: Resources) {
        ChassisType.all.forEach { (_, type) -> fontOf(type, resources) }
    }

    /** The file of a style: the role with the family and the weight that the tokens give it. */
    @FontRes
    fun fontOf(type: ChassisType, resources: Resources): Int {
        val family = resources.getString(type.family)
        val weight = resources.getInteger(type.weight)
        val isItalic = resources.getString(type.fontStyle) == "italic"
        for (role in FontRole.entries.filter { resources.getString(it.family) == family }) {
            val file = role.weights.firstOrNull { resources.getInteger(it.weight) == weight } ?: continue
            return (if (isItalic) file.italic else file.upright)
                ?: error("The role ${role.name} has no italic at weight $weight")
        }
        error("No role of the tokens has $family at weight $weight")
    }

    /** The name of the file of a style, to show. */
    fun fileName(type: ChassisType, resources: Resources): String =
        resources.getResourceEntryName(fontOf(type, resources))
}

private class FontWeightFile(
    @param:IntegerRes val weight: Int,
    @param:FontRes val upright: Int,
    @param:FontRes val italic: Int? = null,
)

/** The font roles of the tokens, with the family and the weights of each in this brand. */
private enum class FontRole(@param:StringRes val family: Int, val weights: List<FontWeightFile>) {
    Display(
        R.string.typography_font_family_display,
        listOf(
            FontWeightFile(R.integer.typography_font_weight_display_elegant_weight, R.font.display_elegant, R.font.display_elegant_italic),
            FontWeightFile(R.integer.typography_font_weight_display_normal_weight, R.font.display_normal, R.font.display_normal_italic),
            FontWeightFile(R.integer.typography_font_weight_display_strong_weight, R.font.display_strong, R.font.display_strong_italic),
            FontWeightFile(R.integer.typography_font_weight_display_mass_weight, R.font.display_mass, R.font.display_mass_italic),
        ),
    ),
    Ui(
        R.string.typography_font_family_ui,
        listOf(
            FontWeightFile(R.integer.typography_font_weight_ui_elegant_weight, R.font.ui_elegant, R.font.ui_elegant_italic),
            FontWeightFile(R.integer.typography_font_weight_ui_normal_weight, R.font.ui_normal, R.font.ui_normal_italic),
            FontWeightFile(R.integer.typography_font_weight_ui_strong_weight, R.font.ui_strong, R.font.ui_strong_italic),
            FontWeightFile(R.integer.typography_font_weight_ui_mass_weight, R.font.ui_mass, R.font.ui_mass_italic),
        ),
    ),
    Prose(
        R.string.typography_font_family_prose,
        listOf(
            FontWeightFile(R.integer.typography_font_weight_prose_elegant_weight, R.font.prose_elegant, R.font.prose_elegant_italic),
            FontWeightFile(R.integer.typography_font_weight_prose_normal_weight, R.font.prose_normal, R.font.prose_normal_italic),
            FontWeightFile(R.integer.typography_font_weight_prose_strong_weight, R.font.prose_strong, R.font.prose_strong_italic),
            FontWeightFile(R.integer.typography_font_weight_prose_mass_weight, R.font.prose_mass, R.font.prose_mass_italic),
        ),
    ),
    Code(
        R.string.typography_font_family_code,
        listOf(
            FontWeightFile(R.integer.typography_font_weight_code_normal_weight, R.font.code_normal),
            FontWeightFile(R.integer.typography_font_weight_code_strong_weight, R.font.code_strong),
        ),
    ),
}
