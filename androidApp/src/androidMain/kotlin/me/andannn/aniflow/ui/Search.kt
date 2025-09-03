package me.andannn.aniflow.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel

class SearchViewModel : ViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(searchViewModel: SearchViewModel = koinViewModel()) {
    LazyColumn {
        repeat(1000) {
            item {
                Text("AAA")
            }
        }
    }
}
