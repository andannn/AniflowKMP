package me.andannn.aniflow.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.DetailMediaUiDataProvider
import me.andannn.aniflow.data.model.DetailUiState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailMedia"

class DetailMediaViewModel(
    mediaId: String,
    dataProvider: DetailMediaUiDataProvider,
) : ViewModel() {
    init {
        viewModelScope.launch {
            dataProvider.detailUiSideEffect(forceRefreshFirstTime = true).collect {
                Log.d(TAG, "detailUiSideEffect: status $it")
            }
        }
    }

    val uiState =
        dataProvider.detailUiDataFlow().stateIn(
            viewModelScope,
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5_000),
            initialValue = DetailUiState.Empty,
        )
}

@Composable
fun DetailMedia(
    mediaId: String,
    viewModel: DetailMediaViewModel =
        koinViewModel(
            parameters = { parametersOf(mediaId) },
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailMediaContent(
        uiState = uiState,
        modifier = Modifier,
    )
}

@Composable
private fun DetailMediaContent(
    uiState: DetailUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
        ) {
            item {
                Text(uiState.toString())
            }
        }
    }
}
