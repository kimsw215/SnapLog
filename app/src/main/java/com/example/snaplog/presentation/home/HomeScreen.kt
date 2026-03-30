package com.example.snaplog.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.snaplog.domain.model.Photo

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onPhotoClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onAddClick = onAddClick,
        onPhotoClick = onPhotoClick,
        onTagSelected = viewModel::selectTag
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onAddClick: () -> Unit,
    onPhotoClick: (Long) -> Unit,
    onTagSelected: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            /* 커스텀 확장형 플로팅 버튼 예제 (필요 없으면 주석 처리 하세요) */
            /*var isExpanded by remember { mutableStateOf(false) }
            val rotation by animateFloatAsState(targetValue = if (isExpanded) 45f else 0f)

            Column(horizontalAlignment = Alignment.End) {
                // 펼쳐졌을 때 나타날 작은 버튼들
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        SmallFloatingActionButton(onClick = { *//* 액션 1 *//* }) { Text("A") }
                        SmallFloatingActionButton(onClick = { *//* 액션 2 *//* }) { Text("B") }
                        SmallFloatingActionButton(onClick = { *//* 액션 3 *//* }) { Text("C") }
                    }
                }

                // 메인 버튼 (+ 모양에서 클릭 시 45도 회전하여 x 모양으로 변함)
                FloatingActionButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }*/

            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TagFilterRow(
                selected = uiState.selectedTag,
                onSelectedChange = onTagSelected
            )

            if (uiState.photos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("아직 저장된 사진이 없어요.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.photos) { photo ->
                        PhotoItem(
                            photo = photo,
                            onClick = { onPhotoClick(photo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagFilterRow(
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    val tags = listOf("ALL", "일상", "음식", "풍경", "그 외")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags) { tag ->
            FilterChip(
                selected = selected == tag,
                onClick = { onSelectedChange(tag) },
                label = { Text(tag) }
            )
        }
    }
}

@Composable
private fun PhotoItem(
    photo: Photo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photo.imagePath,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = photo.memo.ifBlank { "메모 없음" },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = photo.tag,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val dummyPhotos = listOf(
        Photo(1, "path1", "오늘의 공부", "일상", 0L),
        Photo(2, "path2", "카페에서 한 컷", "음식", 0L)
    )
    HomeScreenContent(
        uiState = HomeUiState(photos = dummyPhotos, selectedTag = "ALL"),
        onAddClick = {},
        onPhotoClick = {},
        onTagSelected = {}
    )
}
