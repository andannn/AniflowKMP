/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val label: String,
    val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitDropDownMenuButton(
    modifier: Modifier = Modifier,
    selectIndex: Int,
    menuItemList: List<MenuItem>,
    onMenuItemClick: (index: Int) -> Unit,
) {
    var checked by remember { mutableStateOf(false) }
    val selectedItem =
        menuItemList.getOrNull(selectIndex) ?: menuItemList.getOrNull(0)
            ?: error("No menu item found")
    val buttonColor =
        ButtonDefaults.buttonColors()
    SplitButtonLayout(
        modifier = modifier,
        leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = { checked = true },
                colors = buttonColor,
            ) {
                Icon(
                    selectedItem.icon,
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                    contentDescription = "Localized description",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(selectedItem.label)
            }
        },
        trailingButton = {
            Box(modifier = Modifier.wrapContentSize()) {
                SplitButtonDefaults.TrailingButton(
                    checked = checked,
                    colors = buttonColor,
                    onCheckedChange = { checked = it },
                ) {
                    val rotation: Float by
                        animateFloatAsState(
                            targetValue = if (checked) 180f else 0f,
                            label = "Trailing Icon Rotation",
                        )
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        modifier =
                            Modifier.size(SplitButtonDefaults.TrailingIconSize).graphicsLayer {
                                this.rotationZ = rotation
                            },
                        contentDescription = "Localized description",
                    )
                }
                DropdownMenu(expanded = checked, onDismissRequest = { checked = false }) {
                    menuItemList.forEachIndexed { index, item ->
                        val isSelected = index == selectIndex
                        val colors =
                            if (isSelected) {
                                MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.onPrimary,
                                    leadingIconColor = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                MenuDefaults.itemColors()
                            }
                        val backgroundColor =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        DropdownMenuItem(
                            modifier = Modifier.background(backgroundColor),
                            colors = colors,
                            onClick = {
                                checked = false
                                onMenuItemClick(index)
                            },
                            text = { Text(item.label) },
                            leadingIcon = { Icon(item.icon, contentDescription = null) },
                        )
                    }
                }
            }
        },
    )
}
