package com.francotte.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTypeDropDownMenu(onEventSelected: (EnumType) -> Unit) {
    val enumTypes = enumValues<EnumType>()
    var text by remember { mutableStateOf("Choisissez une catégorie") }
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = text,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            enumTypes.forEachIndexed { index, eventType ->
                DropdownMenuItem(
                    text = { Text(eventType.value) },
                    leadingIcon = {
                        Image(
                            modifier = Modifier.size(30.dp),
                            imageVector = eventType.imageVector,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        text = enumTypes[index].value
                        onEventSelected(enumTypes[index])
                        expanded = false
                    })
            }
        }
    }
}


enum class EnumType(val value: String, val imageVector: ImageVector) {
    HOME("Home", Icons.Default.Home),
    CHECK("Check", Icons.Default.Check),
    SEARCH("Search", Icons.Default.Search),
    FAVORITE("Favorite", Icons.Default.Favorite),
    ADD("Add",Icons.Default.Add)
}

