package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectOptionBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    isSingleSelect: Boolean,
    options: List<String>,
    selectedOptions: List<String>,
    onOptionClick: (index: Int) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            LazyColumn {
                itemsIndexed(
                    items = options,
                    key = { index, item -> item },
                ) { index, item ->
                    ListItem(
                        modifier =
                            Modifier
                                .clickable {
                                    onOptionClick(index)
                                }.padding(horizontal = 16.dp),
                        colors =
                            ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                            ),
                        headlineContent = {
                            Text(item)
                        },
                        trailingContent = {
                            if (isSingleSelect) {
                                val selected = selectedOptions.contains(item)
                                RadioButton(
                                    selected = selected,
                                    onClick = null,
                                )
                            } else {
                                Checkbox(
                                    checked = selectedOptions.contains(item),
                                    onCheckedChange = {
                                        //
                                    },
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
