package com.example.snaplog.presentation.save

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

@Composable
fun SavePhotoScreen(
    savedStateHandle: SavedStateHandle,
    onNavigateToCamera: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    viewModel: SavePhotoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val imagePath by savedStateHandle.getStateFlow("imagePath", "").collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
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

    SavePhotoContent(
        uiState = uiState,
        onMemoChange = viewModel::onMemoChange,
        onTagChange = viewModel::onTagChange,
        onSaveClick = {
            viewModel.savaPhoto {
                Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                onSave()
            }
        },
        onBackClick = onBack,
        onAddPhotoClick = { showBottomSheet = true }
    )

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
private fun SavePhotoContent(
    uiState: SavePhotoUiState,
    onMemoChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onAddPhotoClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("기록 저장") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                        if (uiState.imagePath.isEmpty()) {
                            Text(
                                text = "사진 없음",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            AsyncImage(
                                model = uiState.imagePath,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
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
                placeholder = { Text("이 사진에 대해 기록해 보세요") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )
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
private fun SavePhotoScreenPreview() {
    SavePhotoContent(
        uiState = SavePhotoUiState(memo = "오늘의 맛있는 점심!", tag = "All"),
        onMemoChange = {},
        onTagChange = {},
        onSaveClick = {},
        onBackClick = {},
        onAddPhotoClick = {}
    )
}