/*
 * Copyright 2025, the AozoraBooks project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("UNCHECKED_CAST")

package me.andannn.aniflow.ui

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LabelImportant
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.SearchCategory
import me.andannn.aniflow.data.model.SearchSource
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.paging.EmptyPageComponent
import me.andannn.aniflow.data.paging.LoadingStatus
import me.andannn.aniflow.data.paging.MediaSearchResultPageComponent
import me.andannn.aniflow.data.paging.PageComponent
import me.andannn.aniflow.data.util.getUserTitleString
import me.andannn.aniflow.data.util.label
import me.andannn.aniflow.ui.widget.MediaItemFilledCard
import me.andannn.aniflow.ui.widget.OptionChips
import me.andannn.aniflow.ui.widget.SelectOptionBottomSheet
import me.andannn.aniflow.ui.widget.TitleWithContent
import me.andannn.aniflow.ui.widget.pagingItems
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

private const val TAG = "SearchInput"

enum class OptionSheetType {
    MEDIA_FORMAT,
    SEASON_YEAR,
    MEDIA_SEASON,
}

data class Option<T : Any>(
    val value: T,
) {
    val classType = value::class
}

data class SeasonYear(
    val year: Int,
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        fun createOptions(): List<SeasonYear> {
            val localYear =
                Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .year

            return (1942..localYear).reversed().map { SeasonYear(it) }
        }
    }
}

class KeyWord(
    val keyword: TextFieldValue =
        TextFieldValue(
            text = "",
            selection = TextRange(0),
        ),
)

interface SearchSourceProvider {
    val searchSource: Flow<SearchSource?>
}

class AnimeSearchOptions : SearchSourceProvider {
    var keyword by mutableStateOf(KeyWord())

    val selectedFormatList = mutableStateSetOf<MediaFormat>()

    var seasonYear by mutableStateOf<SeasonYear?>(null)
        private set

    var mediaSeason by mutableStateOf<MediaSeason?>(null)
        private set

    val options = mutableStateListOf<Option<*>>()

    override val searchSource: Flow<SearchSource.Media.Anime?> =
        snapshotFlow {
            val source =
                SearchSource.Media.Anime(
                    keyword = keyword.keyword.text.ifBlank { null },
                    mediaFormat = if (selectedFormatList.isEmpty()) null else selectedFormatList.toList(),
                    seasonYear = seasonYear?.year?.toString(),
                    season = mediaSeason,
                )
            if (source == SearchSource.Media.Anime.None) null else source
        }

    fun updateSeasonYear(year: SeasonYear) {
        seasonYear = year

        options.replaceIfOptionTypeExistsElseAdd(
            Option(year),
        )
    }

    fun removeSeasonYear(year: SeasonYear) {
        seasonYear = null
        options.removeIfOptionTypeExists(
            Option(year),
        )
    }

    fun updateMediaSeason(mediaSeason: MediaSeason) {
        this.mediaSeason = mediaSeason

        options.replaceIfOptionTypeExistsElseAdd(
            Option(mediaSeason),
        )
    }

    fun removeMediaSeason(mediaSeason: MediaSeason) {
        if (this.mediaSeason == mediaSeason) {
            this.mediaSeason = null
            options.removeIfOptionTypeExists(
                Option(mediaSeason),
            )
        }
    }

    fun setMediaFormat(mediaFormat: MediaFormat) {
        Log.d(TAG, "setMediaFormat: $mediaFormat")
        selectedFormatList.add(mediaFormat)

        options.add(Option(mediaFormat))
    }

    fun removeMediaFormat(mediaFormat: MediaFormat) {
        Log.d(TAG, "removeMediaFormat: $mediaFormat")
        selectedFormatList.remove(mediaFormat)

        options.remove(Option(mediaFormat))
    }

    fun updateKeyword(textField: TextFieldValue) {
        this.keyword = KeyWord(textField)

        options.replaceIfOptionTypeExistsElseAdd(
            Option(KeyWord(textField)),
        )
    }

    fun removeKeyword() {
        this.keyword = KeyWord(TextFieldValue())

        options.removeIfOptionTypeExists(
            Option(KeyWord()),
        )
    }

    fun removeOption(option: Option<*>) {
        when (option.value) {
            is MediaFormat -> removeMediaFormat(option.value)
            is SeasonYear -> removeSeasonYear(option.value)
            is MediaSeason -> removeMediaSeason(option.value)
            is KeyWord -> removeKeyword()
            else -> error("Unsupported option type ${option.classType}")
        }
    }

    fun clearAll() {
        selectedFormatList.clear()
        seasonYear = null
        mediaSeason = null
        keyword = KeyWord(TextFieldValue())

        options.clear()
    }
}

@OptIn(FlowPreview::class)
class SearchInputViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val userOptions =
        authRepository.getUserOptionsFlow().stateIn(
            viewModelScope,
            initialValue = UserOptions(),
            started = SharingStarted.WhileSubscribed(5000),
        )
    var selectedCategory by mutableStateOf(SearchCategory.ANIME)

    var visibleOptionSheet by mutableStateOf<OptionSheetType?>(null)

    val animeSearchOptions = AnimeSearchOptions()

    val currentOptions: List<Option<*>>
        get() =
            when (selectedCategory) {
                SearchCategory.ANIME -> animeSearchOptions.options
                else -> emptyList()
            }

    val currentTextField: TextFieldValue
        get() =
            when (selectedCategory) {
                SearchCategory.ANIME -> animeSearchOptions.keyword.keyword
                else -> TextFieldValue()
            }

    private val selectCategoryFlow =
        snapshotFlow { selectedCategory }
            .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchSource: Flow<SearchSource?> =
        selectCategoryFlow.flatMapLatest {
            when (it) {
                SearchCategory.ANIME -> animeSearchOptions.searchSource
                else -> flow { emit(null) }
            }
        }

    var searchResultPagingController by mutableStateOf<PageComponent<*>>(EmptyPageComponent)

    init {
        viewModelScope.launch {
            searchSource
                .distinctUntilChanged()
                .debounce(500.milliseconds)
                .collectLatest { source ->
                    Napier.d(tag = TAG) { "Search source updated: $source" }
                    searchResultPagingController.dispose()
                    searchResultPagingController = EmptyPageComponent

                    searchResultPagingController =
                        when (source) {
                            is SearchSource.Media.Anime -> {
                                MediaSearchResultPageComponent(source = source)
                            }

                            else -> EmptyPageComponent
                        }
                }
        }
    }

    fun onMediaFormatClick(mediaFormat: MediaFormat) {
        if (animeSearchOptions.selectedFormatList.contains(mediaFormat)) {
            animeSearchOptions.removeMediaFormat(mediaFormat)
        } else {
            animeSearchOptions.setMediaFormat(mediaFormat)
        }
    }

    fun onSeasonYearClick(seasonYear: SeasonYear) {
        if (animeSearchOptions.seasonYear == seasonYear) {
            animeSearchOptions.removeSeasonYear(seasonYear)
        } else {
            animeSearchOptions.updateSeasonYear(seasonYear)
        }
    }

    fun onMediaSeasonClick(mediaSeason: MediaSeason) {
        if (animeSearchOptions.mediaSeason == mediaSeason) {
            animeSearchOptions.removeMediaSeason(mediaSeason)
        } else {
            animeSearchOptions.updateMediaSeason(mediaSeason)
        }
    }

    fun removeOption(option: Option<*>) {
        when (selectedCategory) {
            SearchCategory.ANIME -> animeSearchOptions.removeOption(option)
            else -> {}
        }
    }

    fun onTextFieldValueChange(textFieldValue: TextFieldValue) {
        when (selectedCategory) {
            SearchCategory.ANIME -> animeSearchOptions.updateKeyword(textFieldValue)
            else -> {}
        }
    }

    fun onClearAllClick() {
        when (selectedCategory) {
            SearchCategory.ANIME -> {
                animeSearchOptions.clearAll()
            }

            else -> {}
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
    val userOptions by viewModel.userOptions.collectAsStateWithLifecycle()
    SearchInputContent(
        modifier = modifier,
        inputText = viewModel.currentTextField,
        selectedSource = viewModel.selectedCategory,
        currentOptions = viewModel.currentOptions,
        animeSearchOptions = viewModel.animeSearchOptions,
        searchResultPagingController = viewModel.searchResultPagingController,
        onPop = onPop,
        userOptions = userOptions,
        onTextFieldValueChange = viewModel::onTextFieldValueChange,
        onConfirmedSearch = { },
        onOptionChipClick = {
            viewModel.visibleOptionSheet = it
        },
        onCategorySelect = {
            viewModel.selectedCategory = it
        },
        onLabelChipClick = {
            viewModel.removeOption(it)
        },
        onClearAllClick = {
            viewModel.onClearAllClick()
        },
    )

    if (viewModel.visibleOptionSheet != null) {
        when (viewModel.visibleOptionSheet) {
            OptionSheetType.MEDIA_FORMAT ->
                SelectOptionBottomSheet(
                    title = "Format",
                    isSingleSelect = false,
                    options = MediaFormatOption.options().map { it.label() },
                    selectedOptions = viewModel.animeSearchOptions.selectedFormatList.map { it.label() },
                    onOptionClick = {
                        viewModel.onMediaFormatClick(
                            MediaFormatOption.options()[it],
                        )
                    },
                    onDismissRequest = {
                        viewModel.visibleOptionSheet = null
                    },
                )

            OptionSheetType.SEASON_YEAR -> {
                val options =
                    remember {
                        SeasonYear.createOptions()
                    }
                val selected =
                    viewModel.animeSearchOptions.seasonYear
                        ?.year
                        ?.toString()
                SelectOptionBottomSheet(
                    title = "Season Year",
                    isSingleSelect = true,
                    options = options.map { it.year.toString() },
                    selectedOptions = selected?.let { listOf(it) } ?: emptyList(),
                    onOptionClick = {
                        viewModel.onSeasonYearClick(
                            options[it],
                        )
                    },
                    onDismissRequest = {
                        viewModel.visibleOptionSheet = null
                    },
                )
            }

            OptionSheetType.MEDIA_SEASON -> {
                val options =
                    remember {
                        MediaSeason.entries
                    }
                val selected =
                    viewModel.animeSearchOptions.mediaSeason
                        ?.label()
                SelectOptionBottomSheet(
                    title = "Season",
                    isSingleSelect = true,
                    options = options.map { it.label() },
                    selectedOptions = selected?.let { listOf(it) } ?: emptyList(),
                    onOptionClick = {
                        viewModel.onMediaSeasonClick(
                            options[it],
                        )
                    },
                    onDismissRequest = {
                        viewModel.visibleOptionSheet = null
                    },
                )
            }

            null -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchInputContent(
    modifier: Modifier = Modifier,
    selectedSource: SearchCategory,
    currentOptions: List<Option<*>>,
    animeSearchOptions: AnimeSearchOptions,
    searchResultPagingController: PageComponent<*>,
    inputText: TextFieldValue,
    userOptions: UserOptions,
    onPop: () -> Unit = {},
    onTextFieldValueChange: (TextFieldValue) -> Unit = { },
    onConfirmedSearch: () -> Unit = {},
    onCategorySelect: (SearchCategory) -> Unit = {},
    onOptionChipClick: (OptionSheetType) -> Unit = {},
    onLabelChipClick: (Option<*>) -> Unit = {},
    onClearAllClick: () -> Unit = {},
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
        val items by searchResultPagingController.items.collectAsStateWithLifecycle()
        val status by searchResultPagingController.status.collectAsStateWithLifecycle()

        LazyVerticalStaggeredGrid(
            modifier =
                Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize(),
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                SearchSourceSelection(
                    selectedSource = selectedSource,
                    onSelect = onCategorySelect,
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                SearchOptions(
                    modifier =
                        Modifier
                            .padding(top = 12.dp)
                            .animateContentSize(
                                animationSpec =
                                    spring(
                                        stiffness = Spring.StiffnessMedium,
                                        visibilityThreshold = IntSize.VisibilityThreshold,
                                    ),
                            ),
                    selectedSource = selectedSource,
                    animeSearchOptions = animeSearchOptions,
                    onOptionChipClick = onOptionChipClick,
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                KeyWorkInput(
                    modifier = Modifier.padding(top = 12.dp),
                    inputText = inputText,
                    onTextFieldValueChange = onTextFieldValueChange,
                    onConfirmedSearch = onConfirmedSearch,
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                LabelRow(
                    modifier =
                        Modifier
                            .padding(top = 12.dp)
                            .animateContentSize(
                                animationSpec =
                                    spring(
                                        stiffness = Spring.StiffnessMedium,
                                        visibilityThreshold = IntSize.VisibilityThreshold,
                                    ),
                            ),
                    options = currentOptions,
                    onLabelChipClick = onLabelChipClick,
                    onClearAllClick = onClearAllClick,
                )
            }

            when (searchResultPagingController) {
                is EmptyPageComponent -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
//                            Text("Please enter keyword to search")
                        }
                    }
                }

                is MediaSearchResultPageComponent -> {
                    mediaSearchResultPaging(
                        userTitleLanguage = userOptions.titleLanguage,
                        items = items as List<MediaModel>,
                        status = status,
                        onLoadNextPage = {
                            searchResultPagingController.loadNextPage()
                        },
                    )
                }

                else -> {}
            }
        }
    }
}

fun LazyStaggeredGridScope.mediaSearchResultPaging(
    items: List<MediaModel>,
    status: LoadingStatus,
    onLoadNextPage: () -> Unit,
    userTitleLanguage: UserTitleLanguage,
) {
    pagingItems(
        items = items,
        status = status,
        key = { it.id },
        onLoadNextPage = onLoadNextPage,
        itemContent = { item ->
            val title = item.title.getUserTitleString(userTitleLanguage)
            MediaItemFilledCard(
                modifier = Modifier.padding(4.dp),
                title = title,
                coverImage = item.coverImage,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LabelRow(
    modifier: Modifier,
    options: List<Option<*>>,
    onLabelChipClick: (Option<*>) -> Unit = {},
    onClearAllClick: () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (options.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    imageVector = Icons.AutoMirrored.Filled.LabelImportant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                options.forEach { option ->
                    AssistChip(
                        label = { Text(option.label()) },
                        colors =
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier.size(ToggleButtonDefaults.IconSize),
                            )
                        },
                        onClick = {
                            onLabelChipClick(option)
                        },
                    )
                }
                if (options.isNotEmpty()) {
                    AssistChip(
                        label = { Text("Clear all") },
                        colors =
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                labelColor = MaterialTheme.colorScheme.onError,
                            ),
                        onClick = onClearAllClick,
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(1.dp))
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
    animeSearchOptions: AnimeSearchOptions,
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
                        animeSearchOptions = animeSearchOptions,
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
    animeSearchOptions: AnimeSearchOptions,
    onOptionChipClick: (OptionSheetType) -> Unit = {},
) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        OptionChips(
            initialLabel = "Format",
            selectedOption = animeSearchOptions.selectedFormatList.map { it.label() },
            onClick = {
                onOptionChipClick(OptionSheetType.MEDIA_FORMAT)
            },
        )
        OptionChips(
            initialLabel = "Year",
            selectedOption =
                animeSearchOptions.seasonYear?.let { listOf(it.year.toString()) }
                    ?: emptyList(),
            onClick = {
                onOptionChipClick(OptionSheetType.SEASON_YEAR)
            },
        )
        OptionChips(
            initialLabel = "Season",
            selectedOption =
                animeSearchOptions.mediaSeason?.let { listOf(it.label()) }
                    ?: emptyList(),
            onClick = {
                onOptionChipClick(OptionSheetType.MEDIA_SEASON)
            },
        )
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

private fun Option<*>.label(): String =
    when (val v = value) {
        is MediaFormat -> v.label()
        is SeasonYear -> v.year.toString()
        is MediaSeason -> v.label()
        is KeyWord -> v.keyword.text
        else -> error("Unsupported option type ${v.javaClass}")
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

private fun MutableList<Option<*>>.replaceIfOptionTypeExistsElseAdd(item: Option<Any>) {
    val index = this.indexOfFirst { it.classType == item.classType }
    if (index != -1) {
        this.removeAt(index)
        this.add(index, item)
    } else {
        this.add(item)
    }
}

private fun MutableList<Option<*>>.removeIfOptionTypeExists(item: Option<Any>) {
    val index = this.indexOfFirst { it.classType == item.classType }
    if (index != -1) {
        this.removeAt(index)
    }
}
