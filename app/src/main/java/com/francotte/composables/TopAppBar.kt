package com.francotte.composables

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// AppBar with left icon / title / right icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    text: String,
    leftIcon: ImageVector,
    rightIcon: ImageVector,
    actionIconContentDescription: String? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.LightGray),
    onActionClick: () -> Unit = {},
    onNavigationClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1
            )
        },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = actionIconContentDescription,
                    tint = Color.Black
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        },
        colors = colors,
        modifier = modifier,
    )
}