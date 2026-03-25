package com.edutrack.core.presentation.media

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@Composable
actual fun QrScanner(
    modifier: Modifier,
    onScanned: (QrScanResult) -> Unit,
    onError: (String) -> Unit,
    overlay: @Composable (() -> Unit)?,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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

    val previewView = remember { PreviewView(context) }
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    // Track last scanned value for deduplication (non-Compose state for thread safety)
    val lastScannedRef = remember { arrayOfNulls<String>(1) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Only init camera after permission is granted
    LaunchedEffect(hasPermission) {
        if (!hasPermission) return@LaunchedEffect

        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            cameraProvider = provider

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(executor) { imageProxy ->
                        processFrame(imageProxy, barcodeScanner, lastScannedRef, onScanned)
                    }
                }

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                onError("Failed to start scanner: ${e.message}")
            }
        }, executor)
    }

    // Cleanup on disposal
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
            barcodeScanner.close()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Live camera preview as viewfinder
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Optional overlay (e.g., scan region indicator)
        overlay?.invoke()
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processFrame(
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    lastScannedRef: Array<String?>,
    onScanned: (QrScanResult) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }
    val inputImage = InputImage.fromMediaImage(
        mediaImage,
        imageProxy.imageInfo.rotationDegrees
    )
    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                val value = barcode.rawValue ?: continue
                if (value != lastScannedRef[0]) {
                    lastScannedRef[0] = value
                    val format = mapBarcodeFormat(barcode.format)
                    onScanned(QrScanResult(rawValue = value, format = format))
                }
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

private fun mapBarcodeFormat(format: Int): String = when (format) {
    Barcode.FORMAT_QR_CODE -> "QR_CODE"
    Barcode.FORMAT_AZTEC -> "AZTEC"
    Barcode.FORMAT_CODABAR -> "CODABAR"
    Barcode.FORMAT_CODE_39 -> "CODE_39"
    Barcode.FORMAT_CODE_93 -> "CODE_93"
    Barcode.FORMAT_CODE_128 -> "CODE_128"
    Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
    Barcode.FORMAT_EAN_8 -> "EAN_8"
    Barcode.FORMAT_EAN_13 -> "EAN_13"
    Barcode.FORMAT_ITF -> "ITF"
    Barcode.FORMAT_PDF417 -> "PDF417"
    Barcode.FORMAT_UPC_A -> "UPC_A"
    Barcode.FORMAT_UPC_E -> "UPC_E"
    else -> "UNKNOWN"
}
