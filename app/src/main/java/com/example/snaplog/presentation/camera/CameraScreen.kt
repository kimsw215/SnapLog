package com.example.snaplog.presentation.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.snaplog.R
import com.example.snaplog.presentation.util.rememberPermissionState
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onCaptured: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    // val recentPhotoPath by viewModel.recentPhotoPath.collectAsState() // 썸네일용 주석 처리

    // 상태 관리
    var showSheet by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var aspectRatio by remember { mutableStateOf("4:3") }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    
    // 모드 관리: true면 실제 카메라 렌즈 작동, false면 메뉴 선택 화면
    var isCameraActive by remember { mutableStateOf(false) }

    // 앨범 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            // 튕김 방지: URI 인코딩
            onCaptured(Uri.encode(it.toString())) 
        }
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2000)
            toastMessage = null
        }
    }

    LaunchedEffect(Unit) {
        cameraPermission.launchPermissionRequest()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isCameraActive) "촬영" else "사진 선택") },
                navigationIcon = {
                    IconButton(onClick = { if (isCameraActive) isCameraActive = false else onBack() }) {
                        Text("<")
                    }
                },
                actions = {
                    if (isCameraActive) {
                        IconButton(onClick = { showSheet = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (!isCameraActive) {
                // [문제 5번 반영] 사진 선택 전 프리뷰 화면
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(200.dp),
                            shape = CircleShape,
                            color = Color.DarkGray
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_photo_camera_24),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.padding(48.dp)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("기록할 사진을 가져오세요", color = Color.White)
                    }
                    
                    // 우측 하단 카메라 버튼
                    FloatingActionButton(
                        onClick = { showSheet = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(32.dp)
                            .navigationBarsPadding()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_photo_camera_24),
                            contentDescription = null)
                    }
                }
            } else {
                // 실제 카메라 렌즈 화면
                val ratioMultiplier = when (aspectRatio) {
                    "4:3" -> 3f / 4f
                    "16:9" -> 9f / 16f
                    "1:1" -> 1f
                    else -> 0.75f
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f / ratioMultiplier)
                ) {
                    // key(aspectRatio)를 사용하여 비율 변경 시 카메라를 다시 바인딩함
                    key(aspectRatio, cameraSelector) {
                        CameraPreviewView(
                            modifier = Modifier.fillMaxSize(),
                            cameraSelector = cameraSelector,
                            onUseCase = { imageCapture = it }
                        )
                    }
                }

                // 촬영 버튼들 (실제 카메라 모드일 때만 노출)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            takePhoto(context, imageCapture, flashMode) { path ->
                                // 튕김 방지: URI 인코딩
                                onCaptured(Uri.encode(path))
                            }
                        },
                        shape = CircleShape,
                    ) {
                        Text("촬영")
                    }
                    
                    Button(onClick = {
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        else
                            CameraSelector.DEFAULT_BACK_CAMERA
                    },
                        modifier = Modifier
                            .padding(end = 24.dp)) {
                        Text("전/후")
                    }
                }
            }

            // 토스트 메시지
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
            ) {
                Surface(color = Color.Black.copy(alpha = 0.7f), shape = CircleShape) {
                    Text(
                        text = toastMessage ?: "",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // 바텀 시트 메뉴
        if (showSheet) {
            ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp)) {
                    if (!isCameraActive) {
                        // 초기 화면에서의 선택 메뉴
                        ListItem(
                            headlineContent = { Text("직접 촬영하기") },
                            leadingContent = { Icon(painterResource(R.drawable.outline_photo_camera_24), null) },
                            modifier = Modifier.clickable { 
                                isCameraActive = true
                                showSheet = false 
                            }
                        )
                        ListItem(
                            headlineContent = { Text("앨범에서 선택하기") },
                            leadingContent = { Icon(painterResource(R.drawable.outline_add_photo_alternate_24), null) },
                            modifier = Modifier.clickable { 
                                galleryLauncher.launch("image/*")
                                showSheet = false 
                            }
                        )
                    } else {
                        // 카메라 모드에서의 설정 메뉴
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            // 플래시 및 비율 버튼 (기존 로직 동일)
                            FlashButton(flashMode) { 
                                flashMode = it
                                toastMessage = if(it == ImageCapture.FLASH_MODE_ON) "플래시 켬" else "플래시 끔"
                            }
                            RatioButton(aspectRatio) { aspectRatio = it }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashButton(flashMode: Int, onToggle: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { onToggle(if (flashMode == ImageCapture.FLASH_MODE_OFF) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF) },
            modifier = Modifier.size(64.dp).clip(CircleShape).background(if(flashMode == ImageCapture.FLASH_MODE_ON) Color.Yellow else Color.LightGray)
        ) {
            Icon(painterResource(if(flashMode == ImageCapture.FLASH_MODE_ON) R.drawable.outline_flash_on_24 else R.drawable.outline_flashlight_off_24), null)
        }
        Text("플래시", fontSize = 12.sp)
    }
}

@Composable
private fun RatioButton(currentRatio: String, onSelected: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { onSelected(when(currentRatio){ "4:3" -> "16:9"; "16:9" -> "1:1"; else -> "4:3" }) },
            modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.LightGray)
        ) {
            Text(currentRatio, fontWeight = FontWeight.Bold)
        }
        Text("비율", fontSize = 12.sp)
    }
}

@Composable
private fun CameraPreviewView(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector,
    onUseCase: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val imageCapture = ImageCapture.Builder().setTargetRotation(previewView.display?.rotation ?: Surface.ROTATION_0).build()
                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                    val cameraControl = camera.cameraControl
                    val cameraInfo = camera.cameraInfo
                    val scaleGestureDetector = ScaleGestureDetector(ctx, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(detector: ScaleGestureDetector): Boolean {
                            cameraControl.setZoomRatio((cameraInfo.zoomState.value?.zoomRatio ?: 1f) * detector.scaleFactor)
                            return true
                        }
                    })
                    val gestureDetector = GestureDetector(ctx, object : GestureDetector.SimpleOnGestureListener() {
                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            val action = FocusMeteringAction.Builder(previewView.meteringPointFactory.createPoint(e.x, e.y)).build()
                            cameraControl.startFocusAndMetering(action)
                            return true
                        }
                    })
                    previewView.setOnTouchListener { _, event ->
                        scaleGestureDetector.onTouchEvent(event); gestureDetector.onTouchEvent(event); true
                    }
                    onUseCase(imageCapture)
                } catch (e: Exception) { Log.e("CameraPreviewView", "Binding failed", e) }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

private fun takePhoto(context: Context, imageCapture: ImageCapture?, flashMode: Int, onImageSaved: (String) -> Unit) {
    val imgCap = imageCapture ?: return
    imgCap.flashMode = flashMode
    val photoFile = File(context.getExternalFilesDir("Pictures"), "SNAPLOG_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imgCap.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
            onImageSaved(uri.toString())
        }
        override fun onError(exception: ImageCaptureException) { Log.e("CameraScreen", "Capture failed", exception) }
    })
}
