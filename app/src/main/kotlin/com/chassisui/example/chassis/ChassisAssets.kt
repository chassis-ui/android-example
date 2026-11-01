package com.chassisui.example.chassis

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.chassisui.example.R

/** The icons this app uses. A name that left the release fails to compile. */
enum class ChassisIcon(@param:DrawableRes val drawable: Int) {
    Home(R.drawable.ic_home_solid),
    Search(R.drawable.ic_search_solid),
    Inbox(R.drawable.ic_envelope_solid),
    Account(R.drawable.ic_user_solid),
    Foundations(R.drawable.ic_swatchbook_solid),
    Bell(R.drawable.ic_bell_solid),
    Heart(R.drawable.ic_heart_outline),
    HeartFilled(R.drawable.ic_heart_solid),
    Back(R.drawable.ic_arrow_left_solid),
    ChevronRight(R.drawable.ic_chevron_right_solid),
    Close(R.drawable.ic_xmark_solid),
    Check(R.drawable.ic_check_solid),
    Info(R.drawable.ic_info_solid),
    Email(R.drawable.ic_envelope_outline),
    Lock(R.drawable.ic_lock_solid),
    Eye(R.drawable.ic_eye_outline),
    EyeOff(R.drawable.ic_eye_slash_outline),
    Send(R.drawable.ic_paper_plane_solid),
    Trash(R.drawable.ic_trash_solid),
    Star(R.drawable.ic_star_solid),
    Moon(R.drawable.ic_moon_solid),
    Globe(R.drawable.ic_globe_solid),
    SignOut(R.drawable.ic_arrow_right_from_bracket_solid),
    Palette(R.drawable.ic_palette_solid),
    Grid(R.drawable.ic_grid_solid),
    Cube(R.drawable.ic_cube_solid),
    Sparkles(R.drawable.ic_sparkles_solid),
    Filter(R.drawable.ic_filter_solid),
}

/**
 * The images this app uses. A pair of `_light` and `_dark` files is one name, as the sync puts them
 * in `drawable` and `drawable-night`, and Android picks the appearance.
 */
enum class ChassisImage(@param:DrawableRes val drawable: Int) {
    Logo(R.drawable.logo),
    Library(R.drawable.figma_library),
    Tokens(R.drawable.figma_tokens),
    Docs(R.drawable.figma_docs),
    Gallery(R.drawable.comp_gallery),
    Parts(R.drawable.lego_chassis),
    Platforms(R.drawable.platforms),
}

/** An icon of the release in the color of its surroundings, which grows with the font size. */
@Composable
fun Icon(
    icon: ChassisIcon,
    size: Dp = dimensionResource(R.dimen.size_icon_glyph_medium),
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Image(
        painter = painterResource(icon.drawable),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(size * LocalDensity.current.fontScale),
    )
}
