package com.chassisui.example.chassis

import android.content.res.Configuration
import android.graphics.Color as AndroidColor
import android.view.ContextThemeWrapper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import com.chassisui.example.AppearanceChoice
import com.chassisui.example.R

/**
 * The tokens as the Material theme, for the native controls, under the choice of appearance. The
 * colors are resources, which follow `values-night` by themselves: the choice only changes the
 * configuration they are read in, so the code below has one set of names.
 */
@Composable
fun ChassisTheme(appearance: AppearanceChoice, content: @Composable () -> Unit) {
    val systemIsDark = isSystemInDarkTheme()
    val isDark = when (appearance) {
        AppearanceChoice.System -> systemIsDark
        AppearanceChoice.Light -> false
        AppearanceChoice.Dark -> true
    }

    val base = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val configuration = remember(baseConfiguration, isDark) {
        Configuration(baseConfiguration).apply {
            val night = if (isDark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or night
        }
    }
    val context = remember(base, configuration) {
        ContextThemeWrapper(base, R.style.Theme_Chassis).apply { applyOverrideConfiguration(configuration) }
    }

    // The icons of the system bars follow the appearance too
    val activity = LocalActivity.current as? ComponentActivity
    DisposableEffect(activity, isDark) {
        val bars = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT) { isDark }
        activity?.enableEdgeToEdge(bars, bars)
        onDispose {}
    }

    CompositionLocalProvider(
        LocalConfiguration provides configuration,
        LocalContext provides context,
        LocalResources provides context.resources,
    ) {
        MaterialTheme(
            colorScheme = chassisColorScheme(),
            typography = chassisTypography(),
            shapes = chassisShapes(),
            content = content,
        )
    }
}

@Composable
private fun chassisColorScheme() = lightColorScheme(
    primary = colorResource(R.color.color_context_primary_base_color),
    onPrimary = colorResource(R.color.color_context_primary_contrast_color),
    primaryContainer = colorResource(R.color.color_context_primary_bg_even),
    onPrimaryContainer = colorResource(R.color.color_context_primary_fg_main),
    secondary = colorResource(R.color.color_context_secondary_base_color),
    onSecondary = colorResource(R.color.color_context_secondary_contrast_color),
    background = colorResource(R.color.color_context_default_bg_even),
    onBackground = colorResource(R.color.color_context_default_fg_main),
    surface = colorResource(R.color.color_context_default_bg_main),
    onSurface = colorResource(R.color.color_context_default_fg_main),
    onSurfaceVariant = colorResource(R.color.color_context_default_fg_subtle),
    surfaceContainerLowest = colorResource(R.color.color_context_default_bg_main),
    surfaceContainerLow = colorResource(R.color.color_context_default_bg_main),
    surfaceContainer = colorResource(R.color.color_context_default_bg_main),
    surfaceContainerHigh = colorResource(R.color.color_context_default_bg_main),
    surfaceContainerHighest = colorResource(R.color.color_context_default_bg_even),
    outline = colorResource(R.color.color_context_default_border_subtle),
    outlineVariant = colorResource(R.color.color_context_default_border_subtle),
    error = colorResource(R.color.color_context_danger_base_color),
    onError = colorResource(R.color.color_context_danger_contrast_color),
)

/** The bars and the dialogs of Material read these roles. The screens set their styles directly. */
@Composable
private fun chassisTypography() = Typography(
    headlineMedium = ChassisType.h2.style,
    headlineSmall = ChassisType.h4.style,
    titleLarge = ChassisType.navTitle.style,
    bodyMedium = ChassisType.bodyMedium.style,
    labelLarge = ChassisType.buttonMedium.style,
)

@Composable
private fun chassisShapes() = Shapes(
    extraLarge = RoundedCornerShape(dimensionResource(R.dimen.border_radius_modal_main)),
)

/** A shadow of the tokens, always two layers. */
enum class ChassisShadow(val first: ShadowLayer, val second: ShadowLayer) {
    Card(
        ShadowLayer(
            R.color.shadow_card_main_1_color,
            R.dimen.shadow_card_main_1_blur,
            R.dimen.shadow_card_main_1_spread,
            R.dimen.shadow_card_main_1_offset_x,
            R.dimen.shadow_card_main_1_offset_y,
        ),
        ShadowLayer(
            R.color.shadow_card_main_2_color,
            R.dimen.shadow_card_main_2_blur,
            R.dimen.shadow_card_main_2_spread,
            R.dimen.shadow_card_main_2_offset_x,
            R.dimen.shadow_card_main_2_offset_y,
        ),
    ),
    Small(
        ShadowLayer(
            R.color.shadow_context_small_1_color,
            R.dimen.shadow_context_small_1_blur,
            R.dimen.shadow_context_small_1_spread,
            R.dimen.shadow_context_small_1_offset_x,
            R.dimen.shadow_context_small_1_offset_y,
        ),
        ShadowLayer(
            R.color.shadow_context_small_2_color,
            R.dimen.shadow_context_small_2_blur,
            R.dimen.shadow_context_small_2_spread,
            R.dimen.shadow_context_small_2_offset_x,
            R.dimen.shadow_context_small_2_offset_y,
        ),
    ),
    Medium(
        ShadowLayer(
            R.color.shadow_context_medium_1_color,
            R.dimen.shadow_context_medium_1_blur,
            R.dimen.shadow_context_medium_1_spread,
            R.dimen.shadow_context_medium_1_offset_x,
            R.dimen.shadow_context_medium_1_offset_y,
        ),
        ShadowLayer(
            R.color.shadow_context_medium_2_color,
            R.dimen.shadow_context_medium_2_blur,
            R.dimen.shadow_context_medium_2_spread,
            R.dimen.shadow_context_medium_2_offset_x,
            R.dimen.shadow_context_medium_2_offset_y,
        ),
    ),
    Large(
        ShadowLayer(
            R.color.shadow_context_large_1_color,
            R.dimen.shadow_context_large_1_blur,
            R.dimen.shadow_context_large_1_spread,
            R.dimen.shadow_context_large_1_offset_x,
            R.dimen.shadow_context_large_1_offset_y,
        ),
        ShadowLayer(
            R.color.shadow_context_large_2_color,
            R.dimen.shadow_context_large_2_blur,
            R.dimen.shadow_context_large_2_spread,
            R.dimen.shadow_context_large_2_offset_x,
            R.dimen.shadow_context_large_2_offset_y,
        ),
    ),
}

class ShadowLayer(
    @param:ColorRes val color: Int,
    @param:DimenRes val blur: Int,
    @param:DimenRes val spread: Int,
    @param:DimenRes val x: Int,
    @param:DimenRes val y: Int,
)

@Composable
private fun ShadowLayer.shadow() = Shadow(
    radius = dimensionResource(blur),
    spread = dimensionResource(spread),
    color = colorResource(color),
    offset = DpOffset(dimensionResource(x), dimensionResource(y)),
)

/** Both layers of a shadow, around a shape. */
@Composable
fun Modifier.chassisShadow(shadow: ChassisShadow, shape: Shape): Modifier {
    val first = shadow.first.shadow()
    val second = shadow.second.shadow()
    return dropShadow(shape, first).dropShadow(shape, second)
}

/** An easing of the tokens, in its four parts. */
enum class ChassisEasing(
    @param:DimenRes val x1: Int,
    @param:DimenRes val y1: Int,
    @param:DimenRes val x2: Int,
    @param:DimenRes val y2: Int,
) {
    Standard(
        R.dimen.motion_easing_context_ease_in_and_out_x_1,
        R.dimen.motion_easing_context_ease_in_and_out_y_1,
        R.dimen.motion_easing_context_ease_in_and_out_x_2,
        R.dimen.motion_easing_context_ease_in_and_out_y_2,
    ),
    Page(
        R.dimen.motion_easing_page_transition_x_1,
        R.dimen.motion_easing_page_transition_y_1,
        R.dimen.motion_easing_page_transition_x_2,
        R.dimen.motion_easing_page_transition_y_2,
    ),
    Skeleton(
        R.dimen.motion_easing_skeleton_transition_x_1,
        R.dimen.motion_easing_skeleton_transition_y_1,
        R.dimen.motion_easing_skeleton_transition_x_2,
        R.dimen.motion_easing_skeleton_transition_y_2,
    ),
}

/**
 * The motion of the tokens as animations. Compose follows the animation scale of the system, so
 * they stop when the person turns animations off.
 */
object ChassisMotion {
    @Composable
    fun <T> standard(): TweenSpec<T> = curve(ChassisEasing.Standard, R.integer.motion_timing_context_medium)

    @Composable
    fun <T> page(): TweenSpec<T> = curve(ChassisEasing.Page, R.integer.motion_timing_page_transition)

    @Composable
    fun <T> curve(easing: ChassisEasing, @IntegerRes duration: Int): TweenSpec<T> {
        val resources = LocalResources.current
        return tween(
            durationMillis = integerResource(duration),
            easing = CubicBezierEasing(
                resources.rawFloat(easing.x1),
                resources.rawFloat(easing.y1),
                resources.rawFloat(easing.x2),
                resources.rawFloat(easing.y2),
            ),
        )
    }
}

/** A push and a pop of a stack, at the page timing of the tokens. */
class PageTransitions(private val spec: TweenSpec<IntOffset>) {
    val enter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        { slideInHorizontally(spec) { it } }
    val exit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        { slideOutHorizontally(spec) { -it } }
    val popEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        { slideInHorizontally(spec) { -it } }
    val popExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        { slideOutHorizontally(spec) { it } }
}

@Composable
fun rememberPageTransitions(): PageTransitions {
    val spec = ChassisMotion.page<IntOffset>()
    return remember(spec) { PageTransitions(spec) }
}
