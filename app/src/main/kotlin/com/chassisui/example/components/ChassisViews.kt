package com.chassisui.example.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.chassisui.example.Guide
import com.chassisui.example.R
import com.chassisui.example.Toast
import com.chassisui.example.chassis.ChassisEasing
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisImage
import com.chassisui.example.chassis.ChassisMotion
import com.chassisui.example.chassis.ChassisShadow
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.Icon
import com.chassisui.example.chassis.chassisShadow
import com.chassisui.example.chassis.rawFloat

/** A filter chip of the chip tokens. */
@Composable
fun Chip(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_chip_medium))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .defaultMinSize(minHeight = dimensionResource(R.dimen.size_chip_medium_main))
            .background(
                colorResource(
                    if (isSelected) R.color.color_context_primary_bg_even else R.color.color_context_default_bg_main,
                ),
                shape,
            )
            .border(
                dimensionResource(R.dimen.border_width_context_medium),
                colorResource(
                    if (isSelected) R.color.color_context_primary_border_main else R.color.color_context_default_border_subtle,
                ),
                shape,
            )
            .clip(shape)
            .selectable(selected = isSelected, role = Role.Checkbox, onClick = onClick)
            .padding(
                horizontal = dimensionResource(R.dimen.space_chip_medium_gap),
                vertical = dimensionResource(R.dimen.space_chip_medium_padding_y),
            ),
    ) {
        Text(
            title,
            style = ChassisType.chip.style,
            color = colorResource(
                if (isSelected) R.color.color_context_primary_fg_main else R.color.color_context_default_fg_main,
            ),
            maxLines = 1,
        )
    }
}

/** A count of the badge tokens. */
@Composable
fun Badge(text: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(
                minWidth = dimensionResource(R.dimen.size_badge_small_main),
                minHeight = dimensionResource(R.dimen.size_badge_small_main),
            )
            .background(
                colorResource(R.color.color_context_danger_base_color),
                RoundedCornerShape(dimensionResource(R.dimen.border_radius_badge_small)),
            )
            .padding(horizontal = dimensionResource(R.dimen.space_badge_small_nudge)),
    ) {
        Text(
            text,
            style = ChassisType.badge.style,
            color = colorResource(R.color.color_context_danger_contrast_color),
        )
    }
}

/** The initials of a person. The tokens have no avatar group, so this takes context tokens. */
@Composable
fun Avatar(name: String, size: Dp = dimensionResource(R.dimen.size_context_medium)) {
    val initials = name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("")
    val style = if (size > dimensionResource(R.dimen.size_context_medium)) ChassisType.h4 else ChassisType.labelMedium
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .background(colorResource(R.color.color_context_primary_bg_evident), CircleShape),
    ) {
        // The circle has a fixed size, and the initials stay inside it
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
            Text(initials, style = style.style, color = colorResource(R.color.color_context_primary_fg_main))
        }
    }
}

/** A placeholder while content loads. It pulses at the timing of the skeleton tokens. */
@Composable
fun Skeleton(height: Dp = dimensionResource(R.dimen.size_context_xsmall)) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val opacity by transition.animateFloat(
        initialValue = 1f,
        targetValue = LocalResources.current.rawFloat(R.dimen.opacity_level_40),
        animationSpec = infiniteRepeatable(
            ChassisMotion.curve(ChassisEasing.Skeleton, R.integer.motion_timing_skeleton_transition),
            RepeatMode.Reverse,
        ),
        label = "opacity",
    )
    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
            .alpha(opacity)
            .background(
                colorResource(R.color.color_context_default_dim_subtle),
                RoundedCornerShape(dimensionResource(R.dimen.border_radius_context_medium)),
            ),
    )
}

/** A message of the notification tokens. As a toast it sits above the content for a moment. */
@Composable
fun NotificationBanner(toast: Toast, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_notification_main))
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_notification_gap)),
        modifier = modifier
            .fillMaxWidth()
            .chassisShadow(ChassisShadow.Medium, shape)
            .background(colorResource(R.color.color_context_default_bg_main), shape)
            .border(
                dimensionResource(R.dimen.border_width_context_medium),
                colorResource(R.color.color_context_default_border_subtle),
                shape,
            )
            .padding(
                horizontal = dimensionResource(R.dimen.space_notification_padding_x),
                vertical = dimensionResource(R.dimen.space_notification_padding_y),
            )
            .semantics(mergeDescendants = true) {},
    ) {
        Icon(
            if (toast.isError) ChassisIcon.Info else ChassisIcon.Check,
            dimensionResource(R.dimen.size_notification_icon),
            tint = colorResource(
                if (toast.isError) R.color.color_context_danger_icon_main else R.color.color_context_success_icon_main,
            ),
        )
        Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_unit_2))) {
            Text(
                toast.title,
                style = ChassisType.notificationTitle.style,
                color = colorResource(R.color.color_context_default_fg_main),
            )
            Text(
                toast.message,
                style = ChassisType.notificationMessage.style,
                color = colorResource(R.color.color_context_default_fg_subtle),
            )
        }
    }
}

/** An image of the release at the full width, on the page color. */
@Composable
fun FullWidthImage(image: ChassisImage, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(image.drawable),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.color_context_default_bg_even)),
    )
}

/** A card of the card tokens, with an image of the assets. */
@Composable
fun GuideCard(guide: Guide, onClick: () -> Unit) {
    val shape = RoundedCornerShape(dimensionResource(R.dimen.border_radius_card_main))
    Column(
        Modifier
            .fillMaxWidth()
            .chassisShadow(ChassisShadow.Card, shape)
            .background(colorResource(R.color.color_context_default_bg_main), shape)
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        FullWidthImage(guide.image)
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_2xsmall)),
            modifier = Modifier.padding(dimensionResource(R.dimen.space_context_medium)),
        ) {
            Text(
                guide.category.title,
                style = ChassisType.cardSubtitle.style,
                color = colorResource(R.color.color_context_primary_fg_main),
            )
            Text(
                guide.title,
                style = ChassisType.cardTitle.style,
                color = colorResource(R.color.color_context_default_fg_main),
            )
            Text(
                guide.summary,
                style = ChassisType.cardBody.style,
                color = colorResource(R.color.color_context_default_fg_subtle),
            )
        }
    }
}

/** A row of the list tokens: an icon or an avatar, its text, and what the row ends with. */
@Composable
fun ListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    onClick: (() -> Unit)? = null,
    leading: @Composable () -> Unit,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val clickable = if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_list_gap)),
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.color_context_default_bg_main))
            .then(clickable)
            .padding(
                horizontal = dimensionResource(R.dimen.space_list_padding_x),
                vertical = dimensionResource(R.dimen.space_list_padding_y),
            ),
    ) {
        leading()
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = ChassisType.labelLarge.style,
                color = colorResource(R.color.color_context_default_fg_main),
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    style = ChassisType.bodySmall.style,
                    color = colorResource(R.color.color_context_default_fg_subtle),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        trailing()
    }
}

/** The icon a row starts with, at the size and the color of the list tokens. */
@Composable
fun ListIcon(icon: ChassisIcon) {
    Icon(icon, dimensionResource(R.dimen.size_list_icon), tint = colorResource(R.color.color_list_icon))
}

/** What a row with a destination ends with. */
@Composable
fun Disclosure() {
    Icon(
        ChassisIcon.ChevronRight,
        dimensionResource(R.dimen.size_icon_glyph_xsmall),
        tint = colorResource(R.color.color_list_disclosure),
    )
}

/** The line between two rows. */
@Composable
fun ListSeparator() {
    HorizontalDivider(
        thickness = dimensionResource(R.dimen.size_asset_separator),
        color = colorResource(R.color.color_context_default_border_subtle),
    )
}

/** A heading above a list section, in the section tokens. */
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        style = ChassisType.sectionHeader.style,
        color = colorResource(R.color.color_section_fg_medium),
        modifier = modifier,
    )
}

/** What a list shows with nothing in it. */
@Composable
fun EmptyState(icon: ChassisIcon, title: String, message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.space_page_medium_padding_x)),
    ) {
        Icon(
            icon,
            dimensionResource(R.dimen.size_icon_glyph_3xlarge),
            tint = colorResource(R.color.color_context_default_icon_slight),
        )
        Text(
            title,
            style = ChassisType.titleSmall.style,
            color = colorResource(R.color.color_context_default_fg_main),
        )
        Text(
            message,
            style = ChassisType.bodySmall.style,
            color = colorResource(R.color.color_context_default_fg_subtle),
            textAlign = TextAlign.Center,
        )
    }
}
