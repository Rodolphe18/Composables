package com.francotte.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.francotte.composables.ui.theme.ComposablesTheme
import com.francotte.composables.ui.theme.Orange
import kotlin.math.absoluteValue
import kotlin.math.min

enum class TooltipPlacement {
    TOP, BOTTOM
}

class TooltipShape(
    private val radius: Dp,
    val tooltipSize: DpSize,
    val tooltipPlacement: TooltipPlacement,
    private val tooltipCenterStart: Dp = Dp.Unspecified
) : Shape {

    @Suppress("LongMethod")
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val tooltipSize = with(density) { tooltipSize.toSize() }
        require(tooltipSize.isSpecified) { "Tooltip size must be specified" }
        var radius = with(density) { radius.toPx() }
        val minDimension = min(size.width.absoluteValue, size.height.absoluteValue - tooltipSize.height)
        if (2f * radius > minDimension) {
            val scale = minDimension / (radius * 2f)
            radius *= scale
        }
        require(radius >= 0f) { "Corner size in Px can't be negative(radius=$radius)!" }
        if (tooltipSize.isEmpty()) {
            // If there is no arrow, this is just a (maybe rounded) rectangle
            return if (radius == 0f) {
                Outline.Rectangle(size.toRect())
            } else {
                Outline.Rounded(RoundRect(size.toRect(), CornerRadius(radius)))
            }
        }
        val w = size.width
        val h = size.height
        val rectTop = when (tooltipPlacement) {
            TooltipPlacement.TOP -> tooltipSize.height
            TooltipPlacement.BOTTOM -> 0f
        }
        val rectBottom = when (tooltipPlacement) {
            TooltipPlacement.TOP -> h
            TooltipPlacement.BOTTOM -> h - tooltipSize.height
        }
        val arrowCenter = with(density) {
            if (tooltipCenterStart.isSpecified) {
                tooltipCenterStart.toPx().coerceIn(tooltipSize.width / 2f, w - tooltipSize.width / 2f)
            } else {
                w / 2f
            }
        }
        val arrowStart = arrowCenter - tooltipSize.width / 2f
        val arrowEnd = arrowStart + tooltipSize.width
        return Outline.Generic(
            Path().apply {
                moveTo(radius, rectTop)
                if (tooltipPlacement == TooltipPlacement.TOP) {
                    lineTo(arrowStart, rectTop)
                    lineTo(arrowCenter, 0f)
                    lineTo(arrowEnd, rectTop)
                }
                lineTo(w - radius, rectTop)
                corner(w - radius, rectTop, Corner.TOP_END, radius)
                lineTo(w, rectBottom - radius)
                corner(w, rectBottom - radius, Corner.BOTTOM_END, radius)
                if (tooltipPlacement == TooltipPlacement.BOTTOM) {
                    lineTo(arrowEnd, rectBottom)
                    lineTo(arrowCenter, h)
                    lineTo(arrowStart, rectBottom)
                }
                lineTo(radius, rectBottom)
                corner(radius, rectBottom, Corner.BOTTOM_START, radius)
                lineTo(0f, rectTop + radius)
                corner(0f, rectTop + radius, Corner.TOP_START, radius)
                close()
            }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TooltipShape

        if (radius != other.radius) return false
        if (tooltipSize != other.tooltipSize) return false
        if (tooltipPlacement != other.tooltipPlacement) return false
        if (tooltipCenterStart != other.tooltipCenterStart) return false

        return true
    }

    override fun hashCode(): Int {
        var result = radius.hashCode()
        result = 31 * result + tooltipSize.hashCode()
        result = 31 * result + tooltipPlacement.hashCode()
        result = 31 * result + tooltipCenterStart.hashCode()
        return result
    }

    fun withCenterStart(centerStart: Dp): TooltipShape {
        if (centerStart == tooltipCenterStart) return this
        return TooltipShape(radius, tooltipSize, tooltipPlacement, centerStart)
    }

    private enum class Corner(val startAngle: Float) {
        TOP_START(180f) {
            override fun toArcRect(startX: Float, startY: Float, radius: Float): Rect {
                return Rect(startX, startY - radius, startX + radius, startY)
            }
        },
        TOP_END(270f) {
            override fun toArcRect(startX: Float, startY: Float, radius: Float): Rect {
                return Rect(startX, startY, startX + radius, startY + radius)
            }
        },
        BOTTOM_START(90f) {
            override fun toArcRect(startX: Float, startY: Float, radius: Float): Rect {
                return Rect(startX - radius, startY - radius, startX, startY)
            }
        },
        BOTTOM_END(0f) {
            override fun toArcRect(startX: Float, startY: Float, radius: Float): Rect {
                return Rect(startX - radius, startY, startX, startY + radius)
            }
        };

        abstract fun toArcRect(startX: Float, startY: Float, radius: Float): Rect
    }

    companion object {
        private fun Path.corner(startX: Float, startY: Float, corner: Corner, radius: Float) {
            if (radius <= 0f) return
            arcTo(corner.toArcRect(startX, startY, radius), corner.startAngle, 90f, false)
        }
    }
}

private val DefaultCornerRadius = 10.dp
private val DefaultTooltipSize = DpSize(20.dp, 10.dp)

@Immutable
enum class DesignTooltipType(val shape: TooltipShape) {
    UP(TooltipShape(DefaultCornerRadius, DefaultTooltipSize, TooltipPlacement.TOP)),
    MIDDLE(TooltipShape(DefaultCornerRadius, DefaultTooltipSize, TooltipPlacement.BOTTOM)) {
        override val backgroundBrush: Brush
            @Composable get() = Brush.verticalGradient(
                listOf(Color.Yellow, Orange)
            )

        override val titleColor: Color
            @Composable get() = Color.White

        override val textColor: Color
            @Composable get() = Color.White
    },
    TAB_BAR(TooltipShape(DefaultCornerRadius, DefaultTooltipSize, TooltipPlacement.BOTTOM));

    open val backgroundBrush: Brush?
        @Composable get() = null

    open val titleColor: Color
        @Composable get() = Color.White

    open val textColor: Color
        @Composable get() = Color.White
}

private val DefaultInnerPadding = 15.dp

@Stable
fun Modifier.inTooltip(type: DesignTooltipType, centerStart: Dp = Dp.Unspecified) = composed {
    val brush = type.backgroundBrush
    val shape = type.shape.withCenterStart(centerStart)
    var modifier = if (brush == null) {
        background(Orange, shape)
    } else {
        background(brush, shape)
    }
    if (type == DesignTooltipType.TAB_BAR && !isSystemInDarkTheme()) {
        modifier = modifier.border(Dp.Hairline, Color.LightGray, shape)
    }
    modifier.padding(
        start = DefaultInnerPadding,
        end = DefaultInnerPadding,
        top = if (shape.tooltipPlacement == TooltipPlacement.TOP) {
            DefaultInnerPadding + shape.tooltipSize.height
        } else {
            DefaultInnerPadding
        },
        bottom = if (shape.tooltipPlacement == TooltipPlacement.BOTTOM) {
            DefaultInnerPadding + shape.tooltipSize.height
        } else {
            DefaultInnerPadding
        }
    )
}

@Composable
fun DesignTooltip(
    type: DesignTooltipType,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    centerStart: Dp = Dp.Unspecified,
    onClosed: () -> Unit
) {
    Row(
        modifier = modifier.inTooltip(type, centerStart),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold,
                color = type.titleColor
            )
            Text(
                text = description,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Normal,
                color = type.textColor
            )
        }
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onClosed
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "close")
        }
    }
}


@Composable
@Preview
private fun TooltipPreview() {
    ComposablesTheme  {
        DesignTooltip(
            type = DesignTooltipType.TAB_BAR,
            title = "Title :",
            description = "Description\nDescription",
            modifier = Modifier.size(400.dp, 120.dp),
            onClosed = {}
        )
    }
}