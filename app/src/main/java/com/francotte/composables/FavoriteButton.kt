package com.francotte.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.francotte.composables.ui.theme.Orange

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    enabled: Boolean = true,
    elevation: Dp= 4.dp,
    colors: FavoriteButtonColors = FavoriteButtonColors(backgroundColor = Color.White, iconColor = Color.LightGray, favoritedBackgroundColor = Orange, favoritedIconColor = Color.White),
    onToggleFavorite: () -> Unit
) {
    val transition = updateTransition(label = "favorite", targetState = isFavorite)
    val backgroundColor by transition.animateColor(label = "backgroundColor") { colors.backgroundColor(it) }
    val iconColor by transition.animateColor(label = "iconColor") { colors.iconColor(it) }
    val stateDescription = if (isFavorite) "en favori" else "pas en favori"
    Box(
        modifier = modifier
            .shadow(elevation, CircleShape, clip = false)
            .background(backgroundColor, CircleShape)
            .clip(CircleShape)
            .toggleable(
                value = isFavorite,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = { onToggleFavorite() }
            )
            .semantics { this.stateDescription = stateDescription },
        propagateMinConstraints = true
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_favorite),
            contentDescription = "heart icon",
            tint = iconColor
        )
    }
}

@Immutable
class FavoriteButtonColors(
    private val backgroundColor: Color,
    private val iconColor: Color,
    private val favoritedBackgroundColor: Color,
    private val favoritedIconColor: Color
) {

    @Stable
    fun backgroundColor(favorited: Boolean): Color {
        return if (favorited) favoritedBackgroundColor else backgroundColor
    }

    @Stable
    fun iconColor(favorited: Boolean): Color {
        return if (favorited) favoritedIconColor else iconColor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FavoriteButtonColors

        if (backgroundColor != other.backgroundColor) return false
        if (iconColor != other.iconColor) return false
        if (favoritedBackgroundColor != other.favoritedBackgroundColor) return false
        if (favoritedIconColor != other.favoritedIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = backgroundColor.hashCode()
        result = 31 * result + iconColor.hashCode()
        result = 31 * result + favoritedBackgroundColor.hashCode()
        result = 31 * result + favoritedIconColor.hashCode()
        return result
    }
}
