package com.example.snaplog.presentation.camera

import android.Manifest
import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.snaplog.R
import com.example.snaplog.presentation.util.rememberPermissionState
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onCaptured: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var showSheet by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var showGrid by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

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
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("촬영") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", color = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            key(cameraSelector) {
                CameraPreviewView(
                    modifier = Modifier.fillMaxSize(),
                    cameraSelector = cameraSelector,
                    onUseCase = { imageCapture = it }
                )
            }

            if (showGrid) {
                GridOverlay()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(56.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.Gray, CircleShape)
                        .clickable {
                            takePhoto(context, imageCapture, flashMode) { path ->
                                onCaptured(path)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                IconButton(
                    onClick = {
                        cameraSelector =
                            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            else
                                CameraSelector.DEFAULT_BACK_CAMERA
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_autorenew_24),
                        contentDescription = "전후 전환",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = toastMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
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
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FlashButton(flashMode) {
                        flashMode = it
                        toastMessage =
                            if (it == ImageCapture.FLASH_MODE_ON) "플래시 켬" else "플래시 끔"
                    }

                    GridButton(showGrid) {
                        showGrid = it
                        toastMessage = if (it) "격자 켬" else "격자 끔"
                    }
                }
            }
        }
    }
}

@Composable
private fun GridOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 1.dp.toPx()
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(size.width / 3f, 0f),
            end = Offset(size.width / 3f, size.height), strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(size.width * 2f / 3f, 0f),
            end = Offset(size.width * 2f / 3f, size.height), strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(0f, size.height / 3f),
            end = Offset(size.width, size.height / 3f), strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(0f, size.height * 2f / 3f),
            end = Offset(size.width, size.height * 2f / 3f), strokeWidth = strokeWidth
        )
    }
}

@Composable
private fun GridButton(showGrid: Boolean, onToggle: (Boolean) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { onToggle(!showGrid) },
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (showGrid) Color.White else Color.LightGray)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_grid_3x3_24),
                contentDescription = null
            )
        }
        Text("격자", fontSize = 12.sp)
    }
}

@Composable
private fun FlashButton(flashMode: Int, onToggle: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {
                onToggle(
                    if (flashMode == ImageCapture.FLASH_MODE_OFF)
                        ImageCapture.FLASH_MODE_ON
                    else
                        ImageCapture.FLASH_MODE_OFF
                )
            },
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (flashMode == ImageCapture.FLASH_MODE_ON) Color.Yellow else Color.LightGray)
        ) {
            Icon(
                painterResource(
                    if (flashMode == ImageCapture.FLASH_MODE_ON)
                        R.drawable.outline_flash_on_24
                    else
                        R.drawable.outline_flashlight_off_24
                ),
                null
            )
        }
        Text("플래시", fontSize = 12.sp)
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
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val imageCapture = ImageCapture.Builder()
                    .setTargetRotation(
                        previewView.display?.rotation ?: Surface.ROTATION_0
                    )
                    .build()

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    val cameraControl = camera.cameraControl
                    val cameraInfo = camera.cameraInfo

                    val scaleGestureDetector = ScaleGestureDetector(
                        ctx,
                        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                            override fun onScale(detector: ScaleGestureDetector): Boolean {
                                cameraControl.setZoomRatio(
                                    (cameraInfo.zoomState.value?.zoomRatio
                                        ?: 1f) * detector.scaleFactor
                                )
                                return true
                            }
                        })

                    val gestureDetector = GestureDetector(
                        ctx,
                        object : GestureDetector.SimpleOnGestureListener() {
                            override fun onSingleTapUp(e: MotionEvent): Boolean {
                                val action = FocusMeteringAction.Builder(
                                    previewView.meteringPointFactory.createPoint(e.x, e.y)
                                ).build()
                                cameraControl.startFocusAndMetering(action)
                                return true
                            }
                        })

                    previewView.setOnTouchListener { _, event ->
                        scaleGestureDetector.onTouchEvent(event)
                        gestureDetector.onTouchEvent(event)
                        true
                    }
                    onUseCase(imageCapture)
                } catch (e: Exception) {
                    Log.e("CameraPreviewView", "Binding failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    flashMode: Int,
    onImageSaved: (String) -> Unit
) {
    val imgCap = imageCapture ?: return
    imgCap.flashMode = flashMode
    val photoFile = File(
        context.getExternalFilesDir("Pictures"),
        "SNAPLOG_${System.currentTimeMillis()}.jpg"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imgCap.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                onImageSaved(uri.toString())
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Capture failed", exception)
            }
        }
    )
}