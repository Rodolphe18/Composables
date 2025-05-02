package com.francotte.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Composable
fun RowScope.BottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
    )
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        tonalElevation = 0.dp,
        content = content
    )
}

@Composable
fun AppBottomBar(
    destinations: List<BottomBarDestination>,
    onNavigateToDestination: (BottomBarDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    BottomBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            BottomBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {  },
                label = { Text(destination.titleText, fontWeight = FontWeight.SemiBold) }
            )
        }
    }
}

enum class BottomBarDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleText: String,
    val route: KClass<*>
) {
    ITEM_1(
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.Favorite,
        titleText = "item 1",
        route = Item1NavigationRoute::class
    ),
    ITEM_2(
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle,
        titleText = "item 2",
        route = Item2NavigationRoute::class
    ),
    ITEM_3(
        selectedIcon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircle,
        titleText = "item 3",
        route = Item3NavigationRoute::class
    )
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: BottomBarDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false


@Serializable
object Item1NavigationRoute

@Serializable
object Item2NavigationRoute

@Serializable
object Item3NavigationRoute
