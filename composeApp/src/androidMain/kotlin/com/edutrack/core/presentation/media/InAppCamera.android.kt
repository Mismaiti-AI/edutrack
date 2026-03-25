package com.edutrack.core.presentation.media

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.edutrack.core.data.media.MediaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
actual fun InAppCamera(
    modifier: Modifier,
    facing: CameraFacing,
    flashMode: FlashMode,
    onCapture: (MediaResult) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) onError("Camera permission denied")
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasPermission) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required", color = Color.White)
        }
        return
    }

    var currentFacing by remember { mutableStateOf(facing) }
    val previewView = remember { PreviewView(context) }
    val executor = remember { ContextCompat.getMainExecutor(context) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(
                when (flashMode) {
                    FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
                    FlashMode.ON -> ImageCapture.FLASH_MODE_ON
                    FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
                }
            )
            .build()
    }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Bind camera on facing change (guarded by permission)
    LaunchedEffect(currentFacing, hasPermission) {
        if (!hasPermission) return@LaunchedEffect
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            cameraProvider = provider
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val selector = when (currentFacing) {
                CameraFacing.BACK -> CameraSelector.DEFAULT_BACK_CAMERA
                CameraFacing.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
            }
            try {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
            } catch (e: Exception) {
                onError("Failed to start camera: ${e.message}")
            }
        }, executor)
    }

    // Cleanup on disposal
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Live camera preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Camera switch button — top right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                .clickable {
                    currentFacing = when (currentFacing) {
                        CameraFacing.BACK -> CameraFacing.FRONT
                        CameraFacing.FRONT -> CameraFacing.BACK
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch Camera",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Capture button — bottom center
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
                .border(3.dp, Color.LightGray, shape = CircleShape)
                .padding(4.dp)
                .background(Color.White, shape = CircleShape)
                .clickable {
                    val photoFile = File(
                        context.cacheDir,
                        "in_app_photo_${System.currentTimeMillis()}.jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                scope.launch {
                                    val result = readPhotoFile(photoFile)
                                    if (result != null) {
                                        onCapture(result)
                                    } else {
                                        onError("Failed to read captured photo")
                                    }
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                onError("Capture failed: ${exception.message}")
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Empty white circle is the capture button visual
        }
    }
}

private suspend fun readPhotoFile(file: File): MediaResult? = withContext(Dispatchers.IO) {
    try {
        val bytes = file.readBytes()
        MediaResult(
            bytes = bytes,
            fileName = file.name,
            mimeType = "image/jpeg",
            size = bytes.size.toLong()
        )
    } catch (_: Exception) {
        null
    } finally {
        file.delete()
    }
}
