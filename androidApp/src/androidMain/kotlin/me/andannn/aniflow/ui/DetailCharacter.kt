/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.DetailCharacterUiDataProvider
import me.andannn.aniflow.data.getNameString
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.DetailCharacterUiState
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.StyledReadingContentFontFamily
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailCharacter"

class DetailCharacterViewModel(
    private val characterId: String,
    private val dataProvider: DetailCharacterUiDataProvider,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            dataProvider.detailUiSideEffect(false).collect {
                Napier.d(tag = TAG) { "DetailStaffViewModel: sync status $it" }
                _isLoading.value = it.isLoading()
            }
        }
    }

    val uiState =
        dataProvider.detailUiDataFlow().stateIn(
            viewModelScope,
            initialValue = DetailCharacterUiState.Empty,
            started = SharingStarted.WhileSubscribed(5000),
        )
}

@Composable
fun DetailCharacter(
    characterId: String,
    viewModel: DetailCharacterViewModel =
        koinViewModel(
            parameters = { parametersOf(characterId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    DetailCharacterContent(
        isLoading = isLoading,
        character = uiState.characterModel,
        options = uiState.userOption,
        onBack = { navigator.popBackStack() },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DetailCharacterContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    character: CharacterModel?,
    options: UserOptions,
    onBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            TopAppBar(
                colors = TopAppBarColors,
                title = {
                    val title =
                        remember(options, character) {
                            character?.name.getNameString(options.staffNameLanguage)
                        }
                    Text(title)
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
            LazyColumn(
                contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
            ) {
                item {
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
                                model = character?.image,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }

                item {
                    val description =
                        remember(character) {
                            character?.description?.let {
                                AnnotatedString.fromHtml(it)
                            }
                        }
                    val text =
                        buildAnnotatedString {
                            Log.d(TAG, "DetailCharacterContent: ${character?.age}")
                            Log.d(TAG, "DetailCharacterContent: ${character?.dateOfBirth}")
                            character?.dateOfBirth?.let {
                                appendItem(
                                    "Birthday",
                                    it.format(),
                                )
                            }
                            character?.age?.let {
                                appendItem("Age", it)
                            }
                            character?.gender?.let {
                                appendItem("Gender", it)
                            }
                            character?.bloodType?.let {
                                appendItem(
                                    "BloodType",
                                    it,
                                )
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
            }
        }
    }
}
