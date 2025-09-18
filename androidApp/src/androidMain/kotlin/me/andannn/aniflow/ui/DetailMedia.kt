/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HotelClass
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.ButtonWithIconContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.DetailMediaUiDataProvider
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.infoString
import me.andannn.aniflow.data.model.DetailUiState
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.launchUri
import me.andannn.aniflow.data.model.relation.MediaModelWithRelationType
import me.andannn.aniflow.data.releasingTimeString
import me.andannn.aniflow.data.submitErrorOfSyncStatus
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.ShapeHelper
import me.andannn.aniflow.ui.theme.StyledReadingContentFontFamily
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.InfoItemHorizon
import me.andannn.aniflow.ui.widget.MediaRelationItem
import me.andannn.aniflow.ui.widget.MenuItem
import me.andannn.aniflow.ui.widget.SplitDropDownMenuButton
import me.andannn.aniflow.ui.widget.TitleWithContent
import me.andannn.aniflow.ui.widget.buildSpecialMessageText
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime

private const val TAG = "DetailMedia"

class DetailMediaViewModel(
    mediaId: String,
    private val dataProvider: DetailMediaUiDataProvider,
    private val mediaRepository: MediaRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    init {
        viewModelScope.launch {
            cancelLastAndRegisterUiSideEffect(force = false)
        }
    }

    val isSideEffectRefreshing = MutableStateFlow(false)
    private var sideEffectJob: Job? = null

    val uiState =
        dataProvider.detailUiDataFlow().stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailUiState.Empty,
        )

    private var toggleFavoriteJob: Job? = null

    fun onPullRefresh() {
        Napier.d(tag = TAG) { "onPullRefresh:" }
        cancelLastAndRegisterUiSideEffect(force = true)
    }

    fun onChangeListItemStatus(status: MediaListStatus) {
        viewModelScope.launch {
            val error =
                mediaRepository.updateMediaListStatus(
                    mediaListId =
                        uiState.value.mediaListItem?.id
                            ?: error("No media list item found"),
                    status = status,
                )

            if (error != null) submitError(error)
        }
    }

    fun onAddToListClick() {
        viewModelScope.launch {
            val error =
                mediaRepository.addNewMediaToList(
                    uiState.value.mediaModel?.id ?: error("No media id found"),
                )
            if (error != null) submitError(error)
        }
    }

    fun onToggleFavoriteClick() {
        if (toggleFavoriteJob != null && toggleFavoriteJob?.isCompleted == false) {
            Napier.d(tag = TAG) { "onToggleFavoriteClick: last job is running, ignore this click" }
            return
        }

        toggleFavoriteJob =
            viewModelScope.launch {
                val error =
                    mediaRepository.toggleMediaItemLike(
                        uiState.value.mediaModel?.id ?: error("toggle Favorite failed"),
                        uiState.value.mediaModel?.type ?: error("toggle Favorite failed"),
                    )
                if (error != null) submitError(error)
            }
    }

    private fun cancelLastAndRegisterUiSideEffect(force: Boolean = false) {
        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect:" }
        sideEffectJob?.cancel()
        sideEffectJob =
            viewModelScope.launch {
                dataProvider
                    .detailUiSideEffect(forceRefreshFirstTime = force)
                    .collect { status ->
                        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect: sync status $status" }
                        isSideEffectRefreshing.value = status.isLoading()

                        submitErrorOfSyncStatus(status)
                    }
            }
    }
}

@Composable
fun DetailMedia(
    mediaId: String,
    viewModel: DetailMediaViewModel =
        koinViewModel(
            parameters = { parametersOf(mediaId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
    uriHandler: UriHandler = LocalUriHandler.current,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isSideEffectRefreshing.collectAsStateWithLifecycle()

    DetailMediaContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        modifier = Modifier,
        onPullRefresh = { viewModel.onPullRefresh() },
        onPop = { navigator.popBackStack() },
        onChangeStatus = viewModel::onChangeListItemStatus,
        onAddToListClick = viewModel::onAddToListClick,
        onToggleFavoriteClick = viewModel::onToggleFavoriteClick,
        onTrailerClick = {
            Napier.d(tag = TAG) { "onTrailerClick: $it" }
            uriHandler.openUri(it)
        },
        onRelationItemClick = { navigator.navigateTo(Screen.DetailMedia(it.media.id)) },
    )

    ErrorHandleSideEffect(viewModel)
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class,
)
@Composable
private fun DetailMediaContent(
    uiState: DetailUiState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onPullRefresh: () -> Unit = {},
    onChangeStatus: (MediaListStatus) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onToggleFavoriteClick: () -> Unit = {},
    onTrackProgressClick: () -> Unit = {},
    onRatingClick: () -> Unit = {},
    onAddToListClick: () -> Unit = {},
    onTrailerClick: (String) -> Unit = {},
    onRelationItemClick: (MediaModelWithRelationType) -> Unit = {},
    onPop: () -> Unit = {},
) {
    val exitAlwaysScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)
    Scaffold(
        modifier = modifier.nestedScroll(exitAlwaysScrollBehavior),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        bottomBar = {
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarColors,
                title = {
                    Text(uiState.title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onPop()
                    }) {
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
            isRefreshing = isRefreshing,
            onPullRefresh = onPullRefresh,
        ) {
            val staffList by rememberUpdatedState(uiState.staffList)
            val banner by rememberUpdatedState(uiState.mediaModel?.bannerImage)

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
                ) {
                    item {
                        if (banner != null) {
                            Surface(
                                shape = MaterialTheme.shapes.largeIncreased,
                            ) {
                                AsyncImage(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(110.dp),
                                    model = banner,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        } else {
                            Spacer(
                                Modifier
                                    .height(1.dp)
                                    .fillMaxWidth(),
                            )
                        }
                    }

                    if (banner != null) {
                        item { Spacer(Modifier.height(ContentSpacing)) }
                    }

                    item {
                        Column {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                            ) {
                                Surface(
                                    modifier = Modifier.weight(2f),
                                    shape = MaterialTheme.shapes.largeIncreased,
                                ) {
                                    val cover by rememberUpdatedState(uiState.mediaModel?.coverImage)
                                    AsyncImage(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 200.dp)
                                                .fillMaxHeight(),
                                        model = cover,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                    )
                                }

                                Spacer(Modifier.width(8.dp))

                                if (uiState.mediaModel != null) {
                                    InfoArea(
                                        modifier =
                                            Modifier
                                                .weight(3f),
                                        mediaModel = uiState.mediaModel,
                                    )
                                } else {
                                    Spacer(Modifier.weight(3f))
                                }
                            }

                            val hashTags by rememberUpdatedState(uiState.mediaModel?.hashtag)
                            if (!hashTags.isNullOrEmpty()) {
                                val items = hashTags ?: emptyList()
                                FlowRow(
                                    modifier = Modifier.padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    items.forEach { hashTag ->
                                        Text(
                                            modifier = Modifier.alignByBaseline(),
                                            text = hashTag,
                                            color = Color(0xFF1DA1F2),
                                        )
                                    }
                                }
                            }

                            val infoString =
                                remember(uiState.mediaModel) {
                                    uiState.mediaModel?.infoString()
                                }
                            if (infoString != null) {
                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = infoString,
                                )
                            }

                            val nextEpisode = uiState.mediaModel?.nextAiringEpisode?.episode
                            val durationUtilAir = uiState.mediaModel?.releasingTimeString()
                            if (nextEpisode != null && durationUtilAir != null) {
                                val primaryColor = MaterialTheme.colorScheme.primary
                                val text =
                                    remember(
                                        nextEpisode,
                                        durationUtilAir,
                                    ) {
                                        buildSpecialMessageText(
                                            "Episode $nextEpisode in $durationUtilAir",
                                            primaryColor,
                                        )
                                    }
                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = text,
                                )
                            }
                        }
                    }

                    item {
                        val relations by rememberUpdatedState(uiState.relations)

                        if (relations.isNotEmpty()) {
                            TitleWithContent(
                                modifier = Modifier.padding(top = ContentSpacing),
                                title = "Relations",
                                showMore = false,
                            ) {
                                LazyRow {
                                    itemsIndexed(
                                        items = relations,
                                        key = { _, item -> item.media.id to item },
                                    ) { index, item ->
                                        val isFirst = index == 0
                                        val isLast = index == relations.lastIndex
                                        val shape =
                                            ShapeHelper.listItemShapeHorizontal(isFirst, isLast)
                                        MediaRelationItem(
                                            modifier = Modifier.padding(horizontal = 1.dp),
                                            mediaRelation = item,
                                            shape = shape,
                                            userTitleLanguage = uiState.userOptions.titleLanguage,
                                            onClick = {
                                                onRelationItemClick(item)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val description by rememberUpdatedState(uiState.mediaModel?.description)
                        if (description != null) {
                            TitleWithContent(
                                modifier = Modifier.padding(top = ContentSpacing),
                                title = "About",
                                showMore = false,
                            ) {
                                Text(
                                    AnnotatedString.fromHtml(description.toString()),
                                    fontFamily = StyledReadingContentFontFamily,
                                    fontSize = 14.sp,
                                    lineHeight = 17.sp,
                                )
                            }
                        }
                    }

                    if (staffList.isNotEmpty()) {
                        item {
                            TitleWithContent(
                                modifier = Modifier.padding(top = ContentSpacing),
                                title = "Staff",
                                onMoreClick = {},
                            )
                        }

                        itemsIndexed(
                            items = staffList,
                            key = { _, item -> item.staff.id to item.role },
                        ) { index, staff ->
                            val isFirst = index == 0
                            val isLast = index == staffList.lastIndex
                            StaffRowItem(
                                modifier = Modifier.padding(vertical = 1.dp),
                                cover = staff.staff.image,
                                staffName = staff.staff.name?.native ?: "",
                                role = staff.role,
                                shape = ShapeHelper.listItemShapeVertical(isFirst, isLast),
                            )
                        }
                    }

                    item {
                        val trailer by rememberUpdatedState(uiState.mediaModel?.trailer)
                        if (trailer?.id != null) {
                            TitleWithContent(
                                modifier = Modifier.padding(top = ContentSpacing),
                                title = "Trailer",
                                showMore = false,
                            ) {
                                Surface(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16.div(9f)),
                                    shape = MaterialTheme.shapes.largeIncreased,
                                    onClick = {
                                        val uri = trailer?.launchUri()
                                        if (uri != null) {
                                            onTrailerClick(uri)
                                        }
                                    },
                                ) {
                                    AsyncImage(
                                        model = trailer?.thumbnail,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            }
                        }
                    }

                    item {
                        val studios by rememberUpdatedState(uiState.studioList)
                        if (studios.isNotEmpty()) {
                            TitleWithContent(
                                modifier = Modifier.padding(top = ContentSpacing),
                                title = "Studio",
                                showMore = false,
                            ) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    studios.forEach { studio ->
                                        OutlinedButton(
                                            onClick = {},
                                        ) {
                                            Text(studio.name)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val externalLinks by rememberUpdatedState(uiState.mediaModel?.externalLinks)
                        if (!externalLinks.isNullOrEmpty()) {
                            TitleWithContent(
                                modifier = Modifier.padding(top = ContentSpacing),
                                title = "External & Streaming links",
                                showMore = false,
                            ) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    val items = externalLinks ?: emptyList()
                                    items.forEach { externalLink ->
                                        OutlinedButton(
                                            onClick = {},
                                        ) {
                                            if (externalLink.icon != null) {
                                                val tintColor =
                                                    remember(externalLink) {
                                                        externalLink.color?.toComposeColor()
                                                            ?: Color.Black
                                                    }
                                                AsyncImage(
                                                    modifier =
                                                        Modifier
                                                            .size(24.dp)
                                                            .clip(shape = MaterialTheme.shapes.extraSmall),
                                                    colorFilter = ColorFilter.tint(color = tintColor),
                                                    model = externalLink.icon,
                                                    contentDescription = null,
                                                )
                                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            }
                                            Text(externalLink.site)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val authedUser = uiState.authedUser
                val mediaListItem = uiState.mediaListItem

                HorizontalFloatingToolbar(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                    scrollBehavior = exitAlwaysScrollBehavior,
                    expanded = true,
                    leadingContent = {
                        if (mediaListItem != null) {
                            val items = MediaListStatus.entries
                            SplitDropDownMenuButton(
                                menuItemList = MediaListStatus.entries.map { it.toMenuItem() },
                                selectIndex = items.indexOf(mediaListItem.status),
                                onMenuItemClick = {
                                    onChangeStatus(items[it])
                                },
                            )
                        }
                        if (authedUser != null && mediaListItem == null) {
                            Button(
                                colors =
                                    ButtonDefaults.textButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                contentPadding = ButtonWithIconContentPadding,
                                onClick = onAddToListClick,
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Add to List")
                            }
                        }
                    },
                    content = {
                        if (authedUser == null) {
                            Button(
                                colors =
                                    ButtonDefaults.textButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                onClick = onLoginClick,
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Login")
                            }
                        }
                    },
                    trailingContent = {
                        AppBarRow(
                            overflowIndicator = { menuState ->
                                IconButton(
                                    onClick = {
                                        if (menuState.isExpanded) {
                                            menuState.dismiss()
                                        } else {
                                            menuState.show()
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Localized description",
                                    )
                                }
                            },
                        ) {
                            if (authedUser != null) {
                                clickableItem(
                                    onClick = onToggleFavoriteClick,
                                    icon = {
                                        val isFavorite =
                                            rememberUpdatedState(
                                                uiState.mediaModel?.isFavourite == true,
                                            )
                                        if (isFavorite.value) {
                                            Icon(
                                                Icons.Outlined.Favorite,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
                                        } else {
                                            Icon(
                                                Icons.Outlined.FavoriteBorder,
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    label = "Toggle favorite",
                                )
                            }
                            if (authedUser != null && mediaListItem != null) {
                                clickableItem(
                                    onClick = onTrackProgressClick,
                                    icon = {
                                        Icon(Icons.Filled.Bookmarks, contentDescription = null)
                                    },
                                    label = "Track Progress",
                                )
                                clickableItem(
                                    onClick = onRatingClick,
                                    icon = {
                                        Icon(Icons.Filled.StarRate, contentDescription = null)
                                    },
                                    label = "Give rating",
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

private fun MediaListStatus.toMenuItem() =
    when (this) {
        MediaListStatus.CURRENT ->
            MenuItem(
                label = "Watching",
                icon = Icons.Filled.PlayArrow, // 正在看 → 播放图标
            )

        MediaListStatus.PLANNING ->
            MenuItem(
                label = "Planning",
                icon = Icons.Filled.Schedule, // 计划中 → 时钟/日程图标
            )

        MediaListStatus.COMPLETED ->
            MenuItem(
                label = "Completed",
                icon = Icons.Filled.CheckCircle, // 完成 → 绿色对勾
            )

        MediaListStatus.DROPPED ->
            MenuItem(
                label = "Dropped",
                icon = Icons.Filled.Cancel, // 放弃 → 叉/禁用
            )

        MediaListStatus.PAUSED ->
            MenuItem(
                label = "Paused",
                icon = Icons.Filled.PauseCircle, // 暂停 → 暂停按钮
            )

        MediaListStatus.REPEATING ->
            MenuItem(
                label = "Repeating",
                icon = Icons.Filled.Repeat, // 重看 → 循环箭头
            )
    }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StaffRowItem(
    cover: String?,
    staffName: String,
    role: String?,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(90.dp),
        shape = shape,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                modifier = Modifier.width(72.dp),
                model = cover,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(staffName)
                Spacer(modifier = Modifier.weight(1f))
                if (role != null) {
                    Text(
                        role,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun InfoArea(
    mediaModel: MediaModel?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val mediaYear by rememberUpdatedState(
            mediaModel?.seasonYear,
        )

        val allTimeRatedRank by rememberUpdatedState(
            mediaModel?.allTimeRatedRank?.toString(),
        )
        if (allTimeRatedRank != null) {
            InfoItemHorizon(
                icon = Icons.Default.HotelClass,
                contentText = "#$allTimeRatedRank Highest Rated All Time",
            )
        }

        val currentYearTimeRatedRank by rememberUpdatedState(
            mediaModel?.currentYearRatedRank?.toString(),
        )
        if (currentYearTimeRatedRank != null && mediaYear != null) {
            InfoItemHorizon(
                icon = Icons.Default.HotelClass,
                contentText = "#$currentYearTimeRatedRank Highest Rated $mediaYear",
            )
        }

        val allTimePopularRank by rememberUpdatedState(
            mediaModel?.allTimePopularRank?.toString(),
        )
        if (allTimePopularRank != null) {
            InfoItemHorizon(
                icon = Icons.Default.Favorite,
                contentText = "#$allTimePopularRank Most Popular All Time",
            )
        }

        val currentYearRatedRank by rememberUpdatedState(
            mediaModel?.currentYearRatedRank?.toString(),
        )
        if (currentYearRatedRank != null && mediaYear != null && allTimePopularRank == null) {
            InfoItemHorizon(
                icon = Icons.Default.Favorite,
                contentText = "#$currentYearRatedRank Most Popular $mediaYear",
            )
        }

        val averageScore by rememberUpdatedState(
            mediaModel?.averageScore,
        )
        if (averageScore != null) {
            InfoItemHorizon(
                icon = Icons.Filled.BarChart,
                contentText = "Average Score $averageScore%",
            )
        }

        val meanScore by rememberUpdatedState(
            mediaModel?.meanScore,
        )
        if (meanScore != null) {
            InfoItemHorizon(
                icon = Icons.Filled.BarChart,
                contentText = "Mean Score $meanScore%",
            )
        }

        val favourites by rememberUpdatedState(
            mediaModel?.favourites?.toString(),
        )
        if (favourites != null) {
            InfoItemHorizon(
                icon = Icons.Default.ThumbUp,
                contentText = "Favourites $favourites",
            )
        }
    }
}

private val ContentSpacing = 16.dp

private fun String.toComposeColor(): Color {
    val h = removePrefix("#")
    val argb =
        when (h.length) {
            3 -> "FF" + h.flatMap { listOf(it, it) }.joinToString("") // #RGB
            4 -> h.flatMap { listOf(it, it) }.joinToString("") // #ARGB
            6 -> "FF$h" // #RRGGBB
            8 -> h // #AARRGGBB
            else -> error("Bad color: $this")
        }
    val a = argb.substring(0, 2).toInt(16) / 255f
    val r = argb.substring(2, 4).toInt(16) / 255f
    val g = argb.substring(4, 6).toInt(16) / 255f
    val b = argb.substring(6, 8).toInt(16) / 255f
    return Color(r, g, b, a)
}
