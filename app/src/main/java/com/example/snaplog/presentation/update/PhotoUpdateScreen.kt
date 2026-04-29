package com.example.snaplog.presentation.update

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import com.example.snaplog.R
import com.example.snaplog.domain.model.Photo

@Composable
fun PhotoUpdateScreen(
    savedStateHandle: SavedStateHandle,
    photoId: Long,
    onNavigateToCamera: () -> Unit,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    viewModel: PhotoUpdateViewModel = hiltViewModel()
) {
    LaunchedEffect(photoId) {
        if (photoId > 0) viewModel.load(photoId)
    }

    val uiState by viewModel.uiState
    val imagePath by savedStateHandle.getStateFlow("imagePath", "").collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImagePathChange(it.toString()) }
    }

    LaunchedEffect(imagePath) {
        if (imagePath.isNotEmpty()) {
            viewModel.onImagePathChange(imagePath)
        }
    }

    PhotoUpdateContent(
        uiState = uiState,
        onMemoChange = viewModel::onMemoChange,
        onTagChange = viewModel::onTagChange,
        onUpdate = {
            viewModel.update {
                Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                onUpdate()
            }
        },
        onDelete = { showDeleteDialog = true },
        onAddPhotoClick = { showBottomSheet = true },
        onBack = onBack
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("기록 삭제") },
            text = { Text("정말로 기록을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(onDelete) }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    if (showBottomSheet) {
        PhotoSourceBottomSheet(
            onSelectGallery = {
                showBottomSheet = false
                galleryLauncher.launch("image/*")
            },
            onSelectCamera = {
                showBottomSheet = false
                onNavigateToCamera()
            },
            onDeletePhoto = {
                showBottomSheet = false
                viewModel.onImagePathClear()
            },
            onDismiss = {
                showBottomSheet = false
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoUpdateContent(
    uiState: PhotoUpdateUiState,
    onMemoChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    onAddPhotoClick: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("기록 수정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                },
                actions = {
                    TextButton(onClick = onDelete) {
                        Text(
                            text = "삭제",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
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
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TagSelectorRow(
                selected = uiState.tag,
                onSelectedChange = onTagChange
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        if (uiState.photo?.imagePath.isNullOrEmpty()) {
                            Text(
                                text = "사진 없음",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            AsyncImage(
                                model = uiState.photo.imagePath,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Button(
                    onClick = onAddPhotoClick,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "사진 추가",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            OutlinedTextField(
                value = uiState.memo,
                onValueChange = onMemoChange,
                label = { Text("메모") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )

            Button(
                onClick = onUpdate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("수정 완료")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoSourceBottomSheet(
    onSelectGallery: () -> Unit,
    onSelectCamera: () -> Unit,
    onDeletePhoto: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            ListItem(
                headlineContent = { Text("앨범에서 선택") },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.outline_add_photo_alternate_24),
                        contentDescription = null
                    )

                },
                modifier = Modifier.clickable { onSelectGallery() }
            )
            ListItem(
                headlineContent = { Text("사진 찍기") },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { onSelectCamera() }
            )
            ListItem(
                headlineContent = { Text("사진 삭제") },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.outline_delete_forever_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { onDeletePhoto() }
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
private fun PhotoUpdatePreview() {
    val dummyPhoto = Photo(
        id = 1L,
        imagePath = "",
        memo = "지난 주말 가족 여행에서 찍은 사진!",
        tag = "일상",
        createdAt = System.currentTimeMillis()
    )

    PhotoUpdateContent(
        uiState = PhotoUpdateUiState(
            photo = dummyPhoto,
            memo = dummyPhoto.memo,
            tag = dummyPhoto.tag
        ),
        onMemoChange = {},
        onTagChange = {},
        onUpdate = {},
        onDelete = {},
        onAddPhotoClick = {},
        onBack = {}
    )
}