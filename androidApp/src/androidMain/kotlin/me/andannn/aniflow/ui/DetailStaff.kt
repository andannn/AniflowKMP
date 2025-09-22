/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import me.andannn.aniflow.data.DetailStaffUiDataProvider
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.PageComponent
import me.andannn.aniflow.data.StaffCharactersPaging
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.getNameString
import me.andannn.aniflow.data.label
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.DetailStaffUiState
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.SimpleDate
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.relation.VoicedCharacterWithMedia
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.StyledReadingContentFontFamily
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.CharacterWithMediaItem
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.ToggleFavoriteButton
import me.andannn.aniflow.ui.widget.pagingItems
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailStaff"

class DetailStaffViewModel(
    private val staffId: String,
    private val dataProvider: DetailStaffUiDataProvider,
    private val mediaRepository: MediaRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _mediaSort = MutableStateFlow(MediaSort.START_DATE_DESC)
    val mediaSort = _mediaSort.asStateFlow()

    var pagingController by mutableStateOf<PageComponent<VoicedCharacterWithMedia>>(
        PageComponent.empty(),
    )

    private var toggleFavoriteJob: Job? = null

    init {
        viewModelScope.launch {
            dataProvider.detailUiSideEffect(false).collect {
                Napier.d(tag = TAG) { "DetailStaffViewModel: sync status $it" }
                _isLoading.value = it.isLoading()
            }
        }

        viewModelScope.launch {
            _mediaSort.collect { mediaSort ->
                Napier.d(tag = TAG) { "_mediaSort changed: $mediaSort" }
                pagingController.dispose()
                pagingController =
                    StaffCharactersPaging(
                        staffId,
                        mediaSort,
                        errorHandler = this@DetailStaffViewModel,
                    )
            }
        }
    }

    val uiState =
        dataProvider.detailUiDataFlow().stateIn(
            viewModelScope,
            initialValue = DetailStaffUiState.Empty,
            started = SharingStarted.WhileSubscribed(5000),
        )

    fun setMediaSort(sort: MediaSort) {
        _mediaSort.value = sort
    }

    fun onToggleFavoriteClick() {
        if (toggleFavoriteJob != null && toggleFavoriteJob?.isCompleted == false) {
            Napier.d(tag = TAG) { "onToggleFavoriteClick: last job is running, ignore this click" }
            return
        }

        toggleFavoriteJob =
            viewModelScope.launch {
                val error =
                    mediaRepository.toggleStaffItemLike(
                        uiState.value.staffModel?.id ?: error("toggle Favorite failed"),
                    )
                if (error != null) submitError(error)
            }
    }
}

@Composable
fun DetailStaff(
    staffId: String,
    viewModel: DetailStaffViewModel =
        koinViewModel(
            parameters = { parametersOf(staffId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedMediaSort by viewModel.mediaSort.collectAsStateWithLifecycle()
    DetailStaffContent(
        isLoading = isLoading,
        staff = uiState.staffModel,
        options = uiState.userOption,
        selectedMediaSort = selectedMediaSort,
        onSelectMediaSort = viewModel::setMediaSort,
        pagingController = viewModel.pagingController,
        onCharacterClick = {
            navigator.navigateTo(
                Screen.DetailCharacter(it.id),
            )
        },
        onMediaClick = {
            navigator.navigateTo(
                Screen.DetailMedia(it.id),
            )
        },
        onToggleFavoriteClick = viewModel::onToggleFavoriteClick,
        onBack = { navigator.popBackStack() },
    )

    ErrorHandleSideEffect(viewModel)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailStaffContent(
    isLoading: Boolean,
    staff: StaffModel?,
    options: UserOptions,
    selectedMediaSort: MediaSort,
    pagingController: PageComponent<VoicedCharacterWithMedia>,
    modifier: Modifier = Modifier,
    onSelectMediaSort: (MediaSort) -> Unit = {},
    onCharacterClick: (CharacterModel) -> Unit = {},
    onMediaClick: (MediaModel) -> Unit = {},
    onToggleFavoriteClick: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            MediumFlexibleTopAppBar(
                colors = TopAppBarColors,
                scrollBehavior = scrollBehavior,
                title = {
                    val title =
                        remember(options, staff) {
                            staff?.name.getNameString(options.staffNameLanguage)
                        }
                    Text(title)
                },
                subtitle = {
                    staff?.name?.alternative?.let { names ->
                        Text(
                            text = names.joinToString(", "),
                        )
                    }
                },
                actions = {
                    if (staff != null) {
                        ToggleFavoriteButton(
                            isFavorite = staff.isFavourite == true,
                            onClick = onToggleFavoriteClick,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        CustomPullToRefresh(
            modifier =
                Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
                    .background(color = AppBackgroundColor),
            isRefreshing = isLoading,
            enable = false,
        ) {
            val pagingItems = pagingController.items.collectAsStateWithLifecycle()
            val pagingStatus = pagingController.status.collectAsStateWithLifecycle()
            LazyVerticalStaggeredGrid(
                contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
                columns = StaggeredGridCells.Adaptive(160.dp),
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Row {
                        Spacer(Modifier.weight(1f))
                        Surface(
                            modifier =
                                Modifier
                                    .weight(2f)
                                    .fillMaxWidth(),
                            shape = MaterialTheme.shapes.largeIncreased,
                        ) {
                            AsyncImage(
                                model = staff?.image,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    val description =
                        remember(staff) {
                            staff?.description?.let {
                                AnnotatedString.fromHtml(it)
                            }
                        }
                    val text =
                        buildAnnotatedString {
                            staff?.dateOfBirth?.let {
                                appendItem(
                                    "Birth",
                                    it.format(),
                                )
                            }
                            staff?.dateOfDeath?.let {
                                appendItem("Death", it.format())
                            }
                            staff?.age?.let {
                                appendItem("Age", it.toString())
                            }
                            staff?.gender?.let {
                                appendItem("Gender", it)
                            }
                            staff?.yearsActive?.let { activeYear ->
                                val start = activeYear.getOrNull(0)
                                val end = activeYear.getOrNull(1) ?: "Present"
                                appendItem("Years active", "$start-$end")
                            }
                            staff?.homeTown?.let {
                                appendItem("Hometown", it)
                            }
                            description?.let {
                                append(it)
                            }
                        }
                    Text(
                        text = text,
                        fontFamily = StyledReadingContentFontFamily,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                    )
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    var expanded by remember { mutableStateOf(false) }
                    Box(contentAlignment = Alignment.CenterEnd) {
                        Box(
                            modifier =
                                Modifier
                                    .padding(16.dp),
                        ) {
                            TextButton(onClick = { expanded = !expanded }) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Default.FilterAlt, contentDescription = "Filter")
                                    Spacer(Modifier.width(8.dp))
                                    Text(selectedMediaSort.label())
                                }
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                MediaSort.entries.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it.label()) },
                                        onClick = {
                                            onSelectMediaSort(it)
                                            expanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                pagingItems(
                    items = pagingItems.value,
                    status = pagingStatus.value,
                    key = { item -> item.hashCode() },
                    onLoadNextPage = { pagingController.loadNextPage() },
                ) { item ->
                    CharacterWithMediaItem(
                        modifier = Modifier.padding(4.dp),
                        item = item,
                        userTitleLanguage = options.titleLanguage,
                        userStaffLanguage = options.staffNameLanguage,
                        onCharacterClick = {
                            onCharacterClick(item.character)
                        },
                        onMediaClick = {
                            onMediaClick(item.media)
                        },
                    )
                }
            }
        }
    }
}

fun AnnotatedString.Builder.appendItem(
    key: String,
    value: String,
) {
    withStyle(
        style =
            SpanStyle(
                fontWeight = FontWeight.W700,
            ),
    ) {
        append("$key: ")
    }
    append(value)
    append("\n")
}

private fun SimpleDate.toLocalDate(): LocalDate? = day?.let { LocalDate(year, month, it) }

fun SimpleDate.format(): String =
    toLocalDate()?.format(LocalDate.Formats.ISO)
        ?: "%04d-%02d".format(year, month)
