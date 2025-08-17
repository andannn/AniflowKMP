package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.widget.MediaRowItem
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "TrackViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class TrackViewModel(
    private val mediaRepo: MediaRepository,
    private val authRepo: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        Napier.d(tag = TAG) { "DefaultTrackComponent initialized" }
        viewModelScope.launch {
            authRepo
                .getAuthedUser()
                .flatMapLatest { authUser ->
                    if (authUser == null) {
                        // If not authenticated, return an empty flow
                        emptyFlow()
                    } else {
                        mediaRepo.getMediaListFlowByUserId(
                            userId = authUser.id,
                            mediaListStatus =
                                listOf(
                                    MediaListStatus.PLANNING,
                                    MediaListStatus.CURRENT,
                                ),
                            mediaType = MediaType.ANIME,
                        )
                    }
                }.collect { (data, errors) ->
                    Napier.d(tag = TAG) { "data $data" }
                    _state.update {
                        it.copy(
                            items = data ?: emptyList(),
                        )
                    }
                }
        }
    }

    data class UiState(
        val items: List<MediaWithMediaListItem> = emptyList(),
    )
}

@Composable
fun Track(
    modifier: Modifier = Modifier,
    viewModel: TrackViewModel = koinViewModel(),
) {
    val content by viewModel.state.collectAsStateWithLifecycle()
    TrackContent(
        content = content.items,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackContent(
    content: List<MediaWithMediaListItem>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Track") },
            )
        },
    ) {
        LazyColumn(Modifier.padding(top = it.calculateTopPadding())) {
            items(
                items = content,
                key = { it.mediaListModel.id },
            ) { item ->
                val media = item.mediaModel
                MediaRowItem(
                    title = media.title?.romaji.toString(),
                    coverImage = media.coverImage,
                )
            }
        }
    }
}
