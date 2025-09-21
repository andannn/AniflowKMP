/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("UNCHECKED_CAST")

package me.andannn.aniflow.ui

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.CharacterSearchResultPageComponent
import me.andannn.aniflow.data.EmptyPageComponent
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.LoadingStatus
import me.andannn.aniflow.data.MediaSearchResultPageComponent
import me.andannn.aniflow.data.PageComponent
import me.andannn.aniflow.data.StaffSearchResultPageComponent
import me.andannn.aniflow.data.StudioSearchResultPageComponent
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.getNameString
import me.andannn.aniflow.data.getUserTitleString
import me.andannn.aniflow.data.label
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.SearchCategory
import me.andannn.aniflow.data.model.SearchSource
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.widget.CommonItemFilledCard
import me.andannn.aniflow.ui.widget.OptionChips
import me.andannn.aniflow.ui.widget.SelectOptionBottomSheet
import me.andannn.aniflow.ui.widget.TitleWithContent
import me.andannn.aniflow.ui.widget.fullLinePagingItems
import me.andannn.aniflow.ui.widget.pagingItems
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
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

sealed class SearchOptions : SearchSourceProvider {
    var keyword: KeyWord by mutableStateOf(KeyWord())

    val options = mutableStateListOf<Option<*>>()

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

    open fun clearAll() {
        keyword = KeyWord(TextFieldValue())

        options.clear()
    }
}

class AnimeSearchOptions : SearchOptions() {
    val selectedFormatList = mutableStateSetOf<MediaFormat>()

    var seasonYear by mutableStateOf<SeasonYear?>(null)
        private set

    var mediaSeason by mutableStateOf<MediaSeason?>(null)
        private set

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

        options.replaceIfOptionTypeExistsElseAdd(Option(mediaSeason))

        if (seasonYear == null) {
            updateSeasonYear(SeasonYear.createOptions().first())
        }
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
        selectedFormatList.add(mediaFormat)

        options.add(Option(mediaFormat))
    }

    fun removeMediaFormat(mediaFormat: MediaFormat) {
        selectedFormatList.remove(mediaFormat)

        options.remove(Option(mediaFormat))
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

    override fun clearAll() {
        super.clearAll()
        selectedFormatList.clear()
        seasonYear = null
        mediaSeason = null
    }
}

class MangaSearchOptions : SearchOptions() {
    override val searchSource: Flow<SearchSource.Media.Manga?> =
        snapshotFlow {
            val source =
                SearchSource.Media.Manga(
                    keyword = keyword.keyword.text.ifBlank { null },
                )
            if (source == SearchSource.Media.Manga.None) null else source
        }
}

class CharacterSearchOptions : SearchOptions() {
    override val searchSource: Flow<SearchSource.Character?> =
        snapshotFlow {
            val source =
                SearchSource.Character(
                    keyword = keyword.keyword.text.ifBlank { null },
                )
            if (source == SearchSource.Character.None) null else source
        }
}

class StaffSearchOptions : SearchOptions() {
    override val searchSource: Flow<SearchSource.Staff?> =
        snapshotFlow {
            val source =
                SearchSource.Staff(
                    keyword = keyword.keyword.text.ifBlank { null },
                )
            if (source == SearchSource.Staff.None) null else source
        }
}

class StudioSearchOptions : SearchOptions() {
    override val searchSource: Flow<SearchSource.Studio?> =
        snapshotFlow {
            val source =
                SearchSource.Studio(
                    keyword = keyword.keyword.text.ifBlank { null },
                )
            if (source == SearchSource.Studio.None) null else source
        }
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    val userOptions =
        authRepository.getUserOptionsFlow().stateIn(
            viewModelScope,
            initialValue = UserOptions.Default,
            started = SharingStarted.WhileSubscribed(5000),
        )
    var selectedCategory by mutableStateOf(SearchCategory.ANIME)

    var visibleOptionSheet by mutableStateOf<OptionSheetType?>(null)

    val animeSearchOptions = AnimeSearchOptions()
    val mangaSearchOptions = MangaSearchOptions()
    val characterSearchOptions = CharacterSearchOptions()
    val staffSearchOptions = StaffSearchOptions()
    val studioSearchOptions = StudioSearchOptions()

    val currentOptions: SearchOptions by
        derivedStateOf {
            when (selectedCategory) {
                SearchCategory.ANIME -> animeSearchOptions
                SearchCategory.MANGA -> mangaSearchOptions
                SearchCategory.CHARACTER -> characterSearchOptions
                SearchCategory.STAFF -> staffSearchOptions
                SearchCategory.STUDIO -> studioSearchOptions
            }
        }

    private val selectCategoryFlow =
        snapshotFlow { selectedCategory }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchSource: Flow<SearchSource?> =
        selectCategoryFlow.flatMapLatest {
            when (it) {
                SearchCategory.ANIME -> animeSearchOptions.searchSource
                SearchCategory.MANGA -> mangaSearchOptions.searchSource
                SearchCategory.CHARACTER -> characterSearchOptions.searchSource
                SearchCategory.STAFF -> staffSearchOptions.searchSource
                SearchCategory.STUDIO -> studioSearchOptions.searchSource
            }
        }

    var searchResultPagingController by mutableStateOf<PageComponent<*>>(PageComponent.empty<Any>())

    init {
        viewModelScope.launch {
            searchSource
                .distinctUntilChanged()
                .debounce(500.milliseconds)
                .collectLatest { source ->
                    Napier.d(tag = TAG) { "Search source updated: $source" }
                    searchResultPagingController.dispose()

                    searchResultPagingController =
                        when (source) {
                            is SearchSource.Media ->
                                MediaSearchResultPageComponent(
                                    source = source,
                                    errorHandler = this@SearchViewModel,
                                )

                            is SearchSource.Character ->
                                CharacterSearchResultPageComponent(
                                    source = source,
                                    errorHandler = this@SearchViewModel,
                                )

                            is SearchSource.Staff ->
                                StaffSearchResultPageComponent(
                                    source = source,
                                    errorHandler = this@SearchViewModel,
                                )

                            is SearchSource.Studio ->
                                StudioSearchResultPageComponent(
                                    source = source,
                                    errorHandler = this@SearchViewModel,
                                )

                            null -> PageComponent.empty<Any>()
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
            SearchCategory.MANGA -> mangaSearchOptions.updateKeyword(textFieldValue)
            SearchCategory.CHARACTER -> characterSearchOptions.updateKeyword(textFieldValue)
            SearchCategory.STAFF -> staffSearchOptions.updateKeyword(textFieldValue)
            SearchCategory.STUDIO -> studioSearchOptions.updateKeyword(textFieldValue)
        }
    }

    fun onClearAllClick() {
        when (selectedCategory) {
            SearchCategory.ANIME -> animeSearchOptions.clearAll()
            SearchCategory.MANGA -> mangaSearchOptions.clearAll()
            SearchCategory.CHARACTER -> characterSearchOptions.clearAll()
            SearchCategory.STAFF -> staffSearchOptions.clearAll()
            SearchCategory.STUDIO -> studioSearchOptions.clearAll()
        }
    }
}

@Composable
fun Search(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel =
        koinViewModel(
            parameters = { parametersOf("") },
        ),
    router: RootNavigator = LocalRootNavigator.current,
) {
    val userOptions by viewModel.userOptions.collectAsStateWithLifecycle()
    SearchContent(
        modifier = modifier,
        selectedSource = viewModel.selectedCategory,
        currentOptions = viewModel.currentOptions,
        searchResultPagingController = viewModel.searchResultPagingController,
        onPop = {
            router.popBackStack()
        },
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
        onNavigateToScreen = {
            router.navigateTo(it)
        },
    )

    ErrorHandleSideEffect(viewModel)

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
private fun SearchContent(
    modifier: Modifier = Modifier,
    selectedSource: SearchCategory,
    currentOptions: SearchOptions,
    searchResultPagingController: PageComponent<*>,
    userOptions: UserOptions,
    onPop: () -> Unit = {},
    onTextFieldValueChange: (TextFieldValue) -> Unit = { },
    onConfirmedSearch: () -> Unit = {},
    onCategorySelect: (SearchCategory) -> Unit = {},
    onOptionChipClick: (OptionSheetType) -> Unit = {},
    onLabelChipClick: (Option<*>) -> Unit = {},
    onClearAllClick: () -> Unit = {},
    onNavigateToScreen: (Screen) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
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
            contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
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
                    currentOptions = currentOptions,
                    onOptionChipClick = onOptionChipClick,
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                KeyWorkInput(
                    modifier = Modifier.padding(top = 12.dp),
                    inputText = currentOptions.keyword.keyword,
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
                    options = currentOptions.options,
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
                        onClickItem = {
                            onNavigateToScreen(Screen.DetailMedia(it.id))
                        },
                    )
                }

                is CharacterSearchResultPageComponent -> {
                    characterSearchResultPaging(
                        items = items as List<CharacterModel>,
                        status = status,
                        userStaffNameLanguage = userOptions.staffNameLanguage,
                        onLoadNextPage = {
                            searchResultPagingController.loadNextPage()
                        },
                        onClickItem = {
                            onNavigateToScreen(Screen.DetailCharacter(it.id))
                        },
                    )
                }

                is StaffSearchResultPageComponent -> {
                    staffSearchResultPaging(
                        items = items as List<StaffModel>,
                        status = status,
                        userStaffNameLanguage = userOptions.staffNameLanguage,
                        onLoadNextPage = {
                            searchResultPagingController.loadNextPage()
                        },
                        onItemClick = {
                            onNavigateToScreen(Screen.DetailStaff(it.id))
                        },
                    )
                }

                is StudioSearchResultPageComponent -> {
                    studioSearchResultPaging(
                        items = items as List<StudioModel>,
                        status = status,
                        onLoadNextPage = {
                            searchResultPagingController.loadNextPage()
                        },
                    )
                }
            }
        }
    }
}

fun LazyStaggeredGridScope.mediaSearchResultPaging(
    items: List<MediaModel>,
    status: LoadingStatus,
    userTitleLanguage: UserTitleLanguage,
    onLoadNextPage: () -> Unit,
    onClickItem: (MediaModel) -> Unit,
) {
    pagingItems(
        items = items,
        status = status,
        key = { it.id },
        onLoadNextPage = onLoadNextPage,
        itemContent = { item ->
            val title = item.title.getUserTitleString(userTitleLanguage)
            CommonItemFilledCard(
                modifier = Modifier.padding(4.dp),
                title = title,
                coverImage = item.coverImage,
                onClick = {
                    onClickItem(item)
                },
            )
        },
    )
}

fun LazyStaggeredGridScope.characterSearchResultPaging(
    items: List<CharacterModel>,
    userStaffNameLanguage: UserStaffNameLanguage,
    status: LoadingStatus,
    onLoadNextPage: () -> Unit,
    onClickItem: (CharacterModel) -> Unit,
) {
    pagingItems(
        items = items,
        status = status,
        key = { it.id },
        onLoadNextPage = onLoadNextPage,
        itemContent = { item ->
            val title = item.name.getNameString(userStaffNameLanguage)
            CommonItemFilledCard(
                modifier = Modifier.padding(4.dp),
                title = title,
                coverImage = item.image,
                onClick = {
                    onClickItem(item)
                },
            )
        },
    )
}

fun LazyStaggeredGridScope.staffSearchResultPaging(
    items: List<StaffModel>,
    status: LoadingStatus,
    userStaffNameLanguage: UserStaffNameLanguage,
    onLoadNextPage: () -> Unit,
    onItemClick: (StaffModel) -> Unit,
) {
    pagingItems(
        items = items,
        status = status,
        key = { it.id },
        onLoadNextPage = onLoadNextPage,
        itemContent = { item ->
            val title = item.name.getNameString(userStaffNameLanguage)
            CommonItemFilledCard(
                modifier = Modifier.padding(4.dp),
                title = title,
                coverImage = item.image,
                onClick = {
                    onItemClick(item)
                },
            )
        },
    )
}

fun LazyStaggeredGridScope.studioSearchResultPaging(
    items: List<StudioModel>,
    status: LoadingStatus,
    onLoadNextPage: () -> Unit,
) {
    fullLinePagingItems(
        items = items,
        status = status,
        key = { it.id },
        onLoadNextPage = onLoadNextPage,
        itemContent = { item ->
            Card(
                modifier =
                    Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                shape = MaterialTheme.shapes.large,
                onClick = {},
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
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
    modifier: Modifier = Modifier,
    currentOptions: SearchOptions,
    onOptionChipClick: (OptionSheetType) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxWidth()) {
        when (currentOptions) {
            is AnimeSearchOptions ->
                TitleWithContent(
                    modifier = Modifier,
                    title = "Anime Options",
                    showMore = false,
                ) {
                    AnimeSearchOptions(
                        animeSearchOptions = currentOptions,
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
