/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

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
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.withStyle
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import me.andannn.aniflow.data.DetailStaffUiDataProvider
import me.andannn.aniflow.data.getNameString
import me.andannn.aniflow.data.model.DetailStaffUiState
import me.andannn.aniflow.data.model.SimpleDate
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.StyledReadingContentFontFamily
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailStaff"

class DetailStaffViewModel(
    private val staffId: String,
    private val dataProvider: DetailStaffUiDataProvider,
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
            initialValue = DetailStaffUiState.Empty,
            started = SharingStarted.WhileSubscribed(5000),
        )
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
    DetailStaffContent(
        isLoading = isLoading,
        staff = uiState.staffModel,
        options = uiState.userOption,
        onBack = { navigator.popBackStack() },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailStaffContent(
    isLoading: Boolean,
    staff: StaffModel?,
    options: UserOptions,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            TopAppBar(
                colors = TopAppBarColors,
                title = {
                    val title =
                        remember(options, staff) {
                            staff?.name.getNameString(options.staffNameLanguage)
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
                                model = staff?.image,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }

                item {
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
