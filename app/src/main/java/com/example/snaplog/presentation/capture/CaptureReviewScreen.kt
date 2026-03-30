package com.example.snaplog.presentation.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun CaptureReviewScreen(
    imagePath: String,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: CaptureReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    CaptureReviewContent(
        imagePath = imagePath,
        uiState = uiState,
        onMemoChange = viewModel::onMemoChange,
        onTagChange = viewModel::onTagChange,
        onSaveClick = { viewModel.savaPhoto(imagePath, onSaved) },
        onCancelClick = onCancel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CaptureReviewContent(
    imagePath: String,
    uiState: CaptureUiState,
    onMemoChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사진 메모") },
                navigationIcon = {
                    IconButton(onClick = onCancelClick) {
                        Text("<")
                    }
                },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text(
                            text = "저장",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TagSelectorRow(
                selected = uiState.tag,
                onSelectedChange = onTagChange
            )

            AsyncImage(
                model = imagePath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            OutlinedTextField(
                value = uiState.memo,
                onValueChange = onMemoChange,
                label = { Text("메모") },
                placeholder = { Text("이 사진에 대해 기록해 보세요") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun TagSelectorRow(
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    val tags = listOf("All", "일상", "음식", "풍경", "그 외")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            FilterChip(
                selected = selected == tag,
                onClick = { onSelectedChange(tag) },
                label = { Text(tag) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CaptureReviewPreview() {
    CaptureReviewContent(
        imagePath = "",
        uiState = CaptureUiState(memo = "오늘의 맛있는 점심!", tag = "All"),
        onMemoChange = {},
        onTagChange = {},
        onSaveClick = {},
        onCancelClick = {}
    )
}
