package com.francotte.composables

import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.francotte.composables.ui.theme.ComposablesTheme
import kotlin.math.floor
import kotlin.math.round

@Stable
private fun Modifier.clickableRating(onOpenRating: ((Int) -> Unit)?): Modifier {
    return if (onOpenRating == null) this else clickable { onOpenRating(0) }
}

@Composable
fun RatingBar(
    rating: Double,
    style: RatingBarStyle,
    modifier: Modifier = Modifier,
    @IntRange(from = 2) nbStars: Int = RatingBarDefaults.nbStars,
    isReadOnly: Boolean = true,
    hasLabel: Boolean = true,
    onOpenRating: ((Int) -> Unit)? = null,
    onRatingChanged: (Double) -> Unit
) {
    if (rating == 0.0) {
        if (style.defaultLabel == null || !hasLabel) {
            return
        } else {
            // Only one star visible and default label
            Row(
                modifier = modifier.clickableRating(onOpenRating),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBarStar(
                    state = RatingBarStarState.FULL,
                    rating = 1,
                    isReadOnly = true,
                    style = style,
                    onOpenRating = if (onOpenRating == null) null else { _ -> onOpenRating(0) },
                    onRatingChanged = {}
                )
                Text(
                    text = style.defaultLabel.orEmpty(),
                    modifier = Modifier.padding(start = style.labelPadding)
                )
            }
        }
    } else {
        // All stars in one line + label
        Row(
            modifier = modifier.clickableRating(onOpenRating),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingBarStars(rating, nbStars, isReadOnly, style, onOpenRating, onRatingChanged)
            if (hasLabel) {
                Text(
                    text = stringResource(R.string.rating_short, rating),
                    modifier = Modifier.padding(start = style.labelPadding)
                )
            }
        }
    }
}


@Composable
private fun RatingBarStar(
    state: RatingBarStarState,
    rating: Int,
    isReadOnly: Boolean,
    style: RatingBarStyle,
    modifier: Modifier = Modifier,
    onOpenRating: ((Int) -> Unit)?,
    onRatingChanged: (Double) -> Unit
) {
    Icon(
        painter = painterResource(
            id = when (state) {
                RatingBarStarState.EMPTY -> style.emptyStarIcon
                RatingBarStarState.HALF -> style.halfStarIcon
                RatingBarStarState.FULL -> style.fullStarIcon
            }
        ),
        contentDescription = "star icon",
        modifier = modifier.then(
            when {
                isReadOnly && onOpenRating != null -> Modifier.clickable { onOpenRating(rating) }
                isReadOnly -> Modifier
                else -> Modifier.clickable { onRatingChanged(rating.toDouble()) }
            }
        ),
        tint = Color.Unspecified
    )
}

@Composable
private fun RatingBarStars(
    rating: Double,
    nbStars: Int,
    isReadOnly: Boolean,
    style: RatingBarStyle,
    onOpenRating: ((Int) -> Unit)?,
    onRatingChanged: (Double) -> Unit
) {
    val roundedRating = remember(rating, isReadOnly, nbStars) {
        (if (isReadOnly) rating.nearestHalf() else round(rating)).coerceAtMost(nbStars.toDouble())
    }
    // Full stars
    val fullStars = remember(roundedRating) { floor(roundedRating).toInt() }
    for (i in 0 until fullStars) {
        RatingBarStar(
            state = RatingBarStarState.FULL,
            rating = i + 1,
            isReadOnly = isReadOnly,
            style = style,
            onOpenRating = onOpenRating,
            onRatingChanged = onRatingChanged
        )
    }
    // Half star
    val emptyStarsStartIndex = if (roundedRating > fullStars) {
        RatingBarStar(
            state = RatingBarStarState.HALF,
            rating = fullStars + 1,
            isReadOnly = isReadOnly,
            style = style,
            onOpenRating = onOpenRating,
            onRatingChanged = onRatingChanged
        )
        fullStars + 1
    } else {
        fullStars
    }
    // Empty stars
    for (i in emptyStarsStartIndex until nbStars) {
        RatingBarStar(
            state = RatingBarStarState.EMPTY,
            rating = i + 1,
            isReadOnly = isReadOnly,
            style = style,
            onOpenRating = onOpenRating,
            onRatingChanged = onRatingChanged
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun RatingBarPreview() {
    ComposablesTheme {
        Column {
            RatingBar(
                rating = 4.5,
                style = RatingBarDefaults.yellowNumber(),
                onRatingChanged = {},
                onOpenRating = {}
            )
            val (rating, onRatingChanged) = remember { mutableStateOf(0.0) }
            RatingBar(
                rating = rating,
                style = RatingBarDefaults.yellowNumber(),
                onRatingChanged = onRatingChanged,
                isReadOnly = false
            )
        }
    }
}

@Immutable
private enum class RatingBarStarState {
    EMPTY, HALF, FULL
}

@Immutable
sealed interface RatingBarStyle {

    @get:DrawableRes
    val fullStarIcon: Int

    @get:DrawableRes
    val halfStarIcon: Int

    @get:DrawableRes
    val emptyStarIcon: Int

    val labelPadding: Dp

    val defaultLabel: String?

    @Immutable
    data class Number(
        @DrawableRes override val fullStarIcon: Int,
        @DrawableRes override val halfStarIcon: Int,
        @DrawableRes override val emptyStarIcon: Int,
        override val labelPadding: Dp,
        override val defaultLabel: String?
    ) : RatingBarStyle

}

object RatingBarDefaults {

    const val nbStars = 5

    @Composable
    fun yellowNumber(
        @DrawableRes fullStarIcon: Int = R.drawable.ic_star_full_yellow,
        @DrawableRes halfStarIcon: Int = R.drawable.ic_star_half_yellow,
        @DrawableRes emptyStarIcon: Int = R.drawable.ic_star_empty_medium,
        labelPadding: Dp = 5.dp,
        defaultLabel: String? = null
    ): RatingBarStyle.Number {
        return RatingBarStyle.Number(
            fullStarIcon,
            halfStarIcon,
            emptyStarIcon,
            labelPadding,
            defaultLabel
        )
    }

}

fun Double.nearestHalf() = round(this * 2.0) / 2.0