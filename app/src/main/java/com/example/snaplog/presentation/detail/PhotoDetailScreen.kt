package com.example.snaplog.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.snaplog.domain.model.Photo

@Composable
fun PhotoDetailScreen(
    photoId: Long,
    onBack: () -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(photoId) {
        if (photoId > 0) {
            viewModel.load(photoId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    PhotoDetailContent(
        uiState = uiState,
        onBack = onBack,
        onDelete = { viewModel.delete(onBack) },
        onSave = { viewModel.save(onBack) },
        onMemoChange = viewModel::onMemoChange,
        onTagChange = viewModel::onTagChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    onMemoChange: (String) -> Unit,
    onTagChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사진 수정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                },
                actions = {
                    IconButton(onClick = onDelete) {
                        Text("삭제")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading || uiState.photo == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("로딩 중...")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TagSelectorRowDetail(
                    selected = uiState.tag,
                    onSelectedChange = onTagChange
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val imagePath = uiState.photo?.imagePath
                    if (imagePath.isNullOrBlank()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "사진 없음",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        AsyncImage(
                            model = imagePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.memo,
                    onValueChange = onMemoChange,
                    label = { Text("메모") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("수정 완료")
                }
            }
        }
    }
}

@Composable
private fun TagSelectorRowDetail(
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
private fun PhotoDetailPreview() {
    val dummyPhoto = Photo(
        id = 1L,
        imagePath = "", // 빈 경로 테스트
        memo = "지난 주말 가족 여행에서 찍은 사진!",
        tag = "일상",
        createdAt = System.currentTimeMillis()
    )

    PhotoDetailContent(
        uiState = DetailUiState(
            isLoading = false,
            photo = dummyPhoto,
            memo = dummyPhoto.memo,
            tag = dummyPhoto.tag
        ),
        onBack = {},
        onDelete = {},
        onSave = {},
        onMemoChange = {},
        onTagChange = {}
    )
}
