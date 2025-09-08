/*
 * Copyright 2025, the AozoraBooks project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import me.andannn.aniflow.data.model.SearchCategory
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.ui.widget.OptionChips
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

enum class OptionSheetType {
    MEDIA_FORMAT,
}

class SearchInputViewModel(
    initialParam: String,
) : ViewModel() {
    var inputText by
        mutableStateOf(
            TextFieldValue(
                text = initialParam,
                selection = TextRange(initialParam.length),
            ),
        )

    var selectedCategory by mutableStateOf(SearchCategory.ANIME)

    var visibleOptionSheet by mutableStateOf<OptionSheetType?>(null)

    var selectedFormatList = mutableStateSetOf<MediaFormat>()

    fun onMediaFormatClick(mediaFormat: MediaFormat) {
        selectedFormatList.removeIfExistsElseAdd(mediaFormat)
    }

    private fun <T> MutableSet<T>.removeIfExistsElseAdd(item: T) {
        if (this.contains(item)) {
            this.remove(item)
        } else {
            this.add(item)
        }
    }
}

@Composable
fun SearchInput(
    modifier: Modifier = Modifier,
    viewModel: SearchInputViewModel =
        koinViewModel(
            parameters = { parametersOf("") },
        ),
    onPop: () -> Unit = {},
    onNavigateToNested: (HomeNestedScreen) -> Unit = {},
) {
    SearchInputContent(
        modifier = modifier,
        inputText = viewModel.inputText,
        selectedSource = viewModel.selectedCategory,
        selectedFormat = viewModel.selectedFormatList.toList(),
        onPop = onPop,
        onTextFieldValueChange = { viewModel.inputText = it },
        onConfirmedSearch = { },
        onOptionChipClick = {
            viewModel.visibleOptionSheet = it
        },
        onCategorySelect = {
            viewModel.selectedCategory = it
        },
    )

    if (viewModel.visibleOptionSheet != null) {
        when (viewModel.visibleOptionSheet) {
            OptionSheetType.MEDIA_FORMAT ->
                MultiSelectOptionBottomSheet(
                    title = "Format",
                    options = MediaFormatOption.options().map { it.label() },
                    selectedOptions = viewModel.selectedFormatList.map { it.label() },
                    onOptionClick = {
                        viewModel.onMediaFormatClick(
                            MediaFormatOption.options()[it],
                        )
                    },
                    onDismissRequest = {
                        viewModel.visibleOptionSheet = null
                    },
                )

            null -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchInputContent(
    modifier: Modifier = Modifier,
    selectedSource: SearchCategory,
    selectedFormat: List<MediaFormat>,
    inputText: TextFieldValue,
    onPop: () -> Unit = {},
    onTextFieldValueChange: (TextFieldValue) -> Unit = { },
    onConfirmedSearch: () -> Unit = {},
    onCategorySelect: (SearchCategory) -> Unit = {},
    onOptionChipClick: (OptionSheetType) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Search")
                },
                navigationIcon = {
                    IconButton(onClick = onPop) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .padding(it)
                    .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            item {
                SearchSourceSelection(
                    selectedSource = selectedSource,
                    onSelect = onCategorySelect,
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                SearchOptions(
                    modifier =
                        Modifier.animateContentSize(
                            animationSpec =
                                spring(
                                    stiffness = Spring.StiffnessMedium,
                                    visibilityThreshold = IntSize.VisibilityThreshold,
                                ),
                        ),
                    selectedSource = selectedSource,
                    selectedFormat = selectedFormat,
                    onOptionChipClick = onOptionChipClick,
                )
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                KeyWorkInput(
                    inputText = inputText,
                    onTextFieldValueChange = onTextFieldValueChange,
                    onConfirmedSearch = onConfirmedSearch,
                )
            }
        }
    }
}

@Composable
fun KeyWorkInput(
    modifier: Modifier = Modifier,
    inputText: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit = { },
    onConfirmedSearch: () -> Unit = {},
) {
    TitleWithContent(
        modifier = modifier.fillMaxWidth(),
        title = "Search",
        showMore = false,
    ) {
        TextField(
            value = inputText,
            onValueChange = {
                onTextFieldValueChange(it)
            },
            placeholder = {
                Text("Keyword")
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
            keyboardActions =
                KeyboardActions(
                    onSearch = {
                        onConfirmedSearch()
                    },
                ),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchSourceSelection(
    modifier: Modifier = Modifier,
    selectedSource: SearchCategory,
    onSelect: (SearchCategory) -> Unit = {},
) {
    TitleWithContent(
        modifier = modifier.fillMaxWidth(),
        title = "Search In",
        showMore = false,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            val entries = SearchCategory.entries
            entries.forEachIndexed { index, label ->
                val item = entries[index]
                ToggleButton(
                    checked = item == selectedSource,
                    onCheckedChange = {
                        onSelect(item)
                    },
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                ) {
                    Icon(
                        if (item == selectedSource) item.checkedIcon() else item.icon(),
                        contentDescription = "Localized description",
                    )
                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(entries[index].label())
                }
            }
        }
    }
}

@Composable
private fun SearchOptions(
    selectedSource: SearchCategory,
    modifier: Modifier = Modifier,
    selectedFormat: List<MediaFormat> = emptyList(),
    onOptionChipClick: (OptionSheetType) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxWidth()) {
        when (selectedSource) {
            SearchCategory.ANIME ->
                TitleWithContent(
                    modifier = Modifier,
                    title = "Anime Options",
                    showMore = false,
                ) {
                    AnimeSearchOptions(
                        selectedFormat = selectedFormat,
                        onOptionChipClick = onOptionChipClick,
                    )
                }

            else -> {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}

@Composable
fun AnimeSearchOptions(
    modifier: Modifier = Modifier,
    selectedFormat: List<MediaFormat> = emptyList(),
    onOptionChipClick: (OptionSheetType) -> Unit = {},
) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        OptionChips(
            initialLabel = "Format",
            selectedOption = selectedFormat.map { it.label() },
            onClick = {
                onOptionChipClick(OptionSheetType.MEDIA_FORMAT)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultiSelectOptionBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
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
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            options.forEachIndexed { index, item ->
                ListItem(
                    colors =
                        ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        ),
                    headlineContent = {
                        Text(item.toString())
                    },
                    trailingContent = {
                        Checkbox(
                            checked = selectedOptions.contains(item),
                            onCheckedChange = {
                                onOptionClick(index)
                            },
                        )
                    },
                )
            }
        }
    }
}

private fun SearchCategory.icon() =
    when (this) {
        SearchCategory.ANIME -> Icons.Outlined.Tv
        SearchCategory.MANGA -> Icons.Outlined.Book
        SearchCategory.CHARACTER -> Icons.Outlined.Face
        SearchCategory.STAFF -> Icons.Outlined.Person
        SearchCategory.STUDIO -> Icons.Outlined.Apartment
    }

private fun SearchCategory.checkedIcon() =
    when (this) {
        SearchCategory.ANIME -> Icons.Filled.Tv
        SearchCategory.MANGA -> Icons.Filled.Book
        SearchCategory.CHARACTER -> Icons.Filled.Face
        SearchCategory.STAFF -> Icons.Filled.Person
        SearchCategory.STUDIO -> Icons.Filled.Apartment
    }

private fun SearchCategory.label() =
    when (this) {
        SearchCategory.ANIME -> "Anime"
        SearchCategory.MANGA -> "Manga"
        SearchCategory.CHARACTER -> "Character"
        SearchCategory.STAFF -> "Staff"
        SearchCategory.STUDIO -> "Studio"
    }

private fun MediaFormat.label() =
    when (this) {
        MediaFormat.TV -> "TV"
        MediaFormat.TV_SHORT -> "TV Short"
        MediaFormat.MOVIE -> "Movie"
        MediaFormat.SPECIAL -> "Special"
        MediaFormat.OVA -> "OVA"
        MediaFormat.ONA -> "ONA"
        MediaFormat.MUSIC -> "Music"
        MediaFormat.MANGA -> "Manga"
        MediaFormat.NOVEL -> "Novel"
        MediaFormat.ONE_SHOT -> "One Shot"
    }

object MediaFormatOption {
    fun options() =
        listOf(
            MediaFormat.TV,
            MediaFormat.TV_SHORT,
            MediaFormat.MOVIE,
            MediaFormat.SPECIAL,
            MediaFormat.OVA,
            MediaFormat.ONA,
            MediaFormat.MUSIC,
        )
}
