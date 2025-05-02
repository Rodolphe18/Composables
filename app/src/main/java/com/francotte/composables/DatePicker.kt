package com.francotte.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(context: Context) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var date by remember { mutableStateOf("") }
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(BorderStroke(0.5.dp, Color.Black), RoundedCornerShape(12.dp))
                .clickable { showDatePicker = true },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = "Choisissez une date", modifier = Modifier.padding(start = 12.dp))
        }
        Spacer(Modifier.height(30.dp))
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDate = Calendar.getInstance().apply {
                                timeInMillis = datePickerState.selectedDateMillis!!
                            }
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                            val resultDate = Date(selectedDate.time.time)
                            val result = sdf.format(resultDate)
                            date = result
                            if (selectedDate.after(Calendar.getInstance())) {
                                Toast.makeText(
                                    context,
                                    "Selected date $result saved",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showDatePicker = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "Selected date should be after today, please select again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        }
                    ) { Text("Cancel") }
                }
            )
            {
                DatePicker(state = datePickerState)
            }
        }
    }

}