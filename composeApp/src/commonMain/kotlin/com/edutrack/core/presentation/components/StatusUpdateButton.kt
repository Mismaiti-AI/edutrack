package com.edutrack.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Status update button with dropdown for allowed next states.
 *
 * Shows a button that opens a dropdown menu with the available
 * status transitions. When a new status is selected, onUpdate is called.
 *
 * Usage:
 * ```
 * StatusUpdateButton(
 *     currentStatus = "requested",
 *     options = listOf("acknowledged" to "Acknowledge", "picked_up" to "Mark Picked Up"),
 *     onUpdate = { newStatus -> viewModel.updateStatus(item.id, newStatus) }
 * )
 * ```
 */
@Composable
fun StatusUpdateButton(
    currentStatus: String,
    options: List<Pair<String, String>>,  // value to label
    modifier: Modifier = Modifier,
    onUpdate: (String) -> Unit
) {
    if (options.isEmpty()) return

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth().padding(top = 12.dp)) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Status")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        onUpdate(value)
                    }
                )
            }
        }
    }
}
