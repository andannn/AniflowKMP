package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionChips(
    modifier: Modifier = Modifier,
    initialLabel: String,
    selectedOption: List<String> = emptyList(),
    onClick: () -> Unit = {},
) {
    val isSelected = selectedOption.isNotEmpty()
    val text =
        when (selectedOption.size) {
            0 -> initialLabel
            1 -> selectedOption.first()
            else -> "${selectedOption.first()} +${selectedOption.size - 1}"
        }
    val color =
        if (isSelected) {
            AssistChipDefaults.assistChipColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        } else {
            AssistChipDefaults.assistChipColors()
        }
    AssistChip(
        modifier = modifier.padding(horizontal = 4.dp),
        onClick = onClick,
        colors = color,
        label = { Text(text) },
        trailingIcon = {
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = null,
                Modifier.size(AssistChipDefaults.IconSize),
            )
        },
    )
}
