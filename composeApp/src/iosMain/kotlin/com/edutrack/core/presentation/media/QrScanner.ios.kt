package com.edutrack.core.presentation.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeAztecCode
import platform.AVFoundation.AVMetadataObjectTypeCode128Code
import platform.AVFoundation.AVMetadataObjectTypeCode39Code
import platform.AVFoundation.AVMetadataObjectTypeCode93Code
import platform.AVFoundation.AVMetadataObjectTypeDataMatrixCode
import platform.AVFoundation.AVMetadataObjectTypeEAN13Code
import platform.AVFoundation.AVMetadataObjectTypeEAN8Code
import platform.AVFoundation.AVMetadataObjectTypeITF14Code
import platform.AVFoundation.AVMetadataObjectTypePDF417Code
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.AVFoundation.AVMetadataObjectTypeUPCECode
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.defaultDeviceWithDeviceType
import platform.AVFoundation.requestAccessForMediaType
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun QrScanner(
    modifier: Modifier,
    onScanned: (QrScanResult) -> Unit,
    onError: (String) -> Unit,
    overlay: @Composable (() -> Unit)?,
) {
    var hasPermission by remember {
        mutableStateOf(
            AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
        )
    }
    var permissionChecked by remember { mutableStateOf(hasPermission) }

    if (!permissionChecked) {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        if (status == AVAuthorizationStatusNotDetermined) {
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                dispatch_async(dispatch_get_main_queue()) {
                    hasPermission = granted
                    permissionChecked = true
                    if (!granted) onError("Camera permission denied")
                }
            }
        } else {
            permissionChecked = true
            hasPermission = status == AVAuthorizationStatusAuthorized
            if (!hasPermission) onError("Camera permission denied")
        }
    }

    if (!hasPermission) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required", color = Color.White)
        }
        return
    }

    val scannerManager = remember { IosQrScannerManager(onScanned) }

    DisposableEffect(Unit) {
        onDispose {
            scannerManager.stopSession()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        UIKitView(
            factory = {
                val containerView = UIView()
                scannerManager.setupSession(
                    view = containerView,
                    onError = onError
                )
                containerView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Optional overlay (e.g., viewfinder box, instruction text)
        overlay?.invoke()
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class IosQrScannerManager(
    private val onScanned: (QrScanResult) -> Unit
) {
    private var session: AVCaptureSession? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var metadataDelegate: MetadataDelegate? = null
    private var metadataQueue: platform.darwin.dispatch_queue_t? = null
    private var lastScannedValue: String? = null

    fun setupSession(view: UIView, onError: (String) -> Unit) {
        val captureSession = AVCaptureSession()
        captureSession.sessionPreset = AVCaptureSessionPresetHigh

        val device = AVCaptureDevice.defaultDeviceWithDeviceType(
            deviceType = AVCaptureDeviceTypeBuiltInWideAngleCamera,
            mediaType = AVMediaTypeVideo,
            position = AVCaptureDevicePositionBack
        )
        if (device == null) {
            onError("Camera not available")
            return
        }

        val input = try {
            AVCaptureDeviceInput(device = device, error = null)
        } catch (_: Exception) {
            onError("Failed to create camera input")
            return
        }

        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        val metadataOutput = AVCaptureMetadataOutput()
        if (captureSession.canAddOutput(metadataOutput)) {
            captureSession.addOutput(metadataOutput)

            val delegate = MetadataDelegate { value, type ->
                if (value != lastScannedValue) {
                    lastScannedValue = value
                    val format = mapMetadataType(type)
                    dispatch_async(dispatch_get_main_queue()) {
                        onScanned(QrScanResult(rawValue = value, format = format))
                    }
                }
            }
            metadataDelegate = delegate

            val queue = dispatch_queue_create("com.mismaiti.qrscanner.metadata", null)
            metadataQueue = queue
            metadataOutput.setMetadataObjectsDelegate(delegate, queue = queue)

            // Set supported barcode types (must be set AFTER adding output to session)
            metadataOutput.metadataObjectTypes = listOf(
                AVMetadataObjectTypeQRCode,
                AVMetadataObjectTypeEAN13Code,
                AVMetadataObjectTypeEAN8Code,
                AVMetadataObjectTypeCode128Code,
                AVMetadataObjectTypeCode39Code,
                AVMetadataObjectTypeCode93Code,
                AVMetadataObjectTypeUPCECode,
                AVMetadataObjectTypePDF417Code,
                AVMetadataObjectTypeAztecCode,
                AVMetadataObjectTypeDataMatrixCode,
                AVMetadataObjectTypeITF14Code,
            )
        }

        val layer = AVCaptureVideoPreviewLayer(session = captureSession)
        layer.videoGravity = AVLayerVideoGravityResizeAspectFill
        layer.frame = view.bounds
        view.layer.addSublayer(layer)

        session = captureSession
        previewLayer = layer

        captureSession.startRunning()
    }

    fun stopSession() {
        session?.stopRunning()
        session = null
        previewLayer?.removeFromSuperlayer()
        previewLayer = null
        metadataDelegate = null
        metadataQueue = null
    }

    private fun mapMetadataType(type: String): String = when (type) {
        AVMetadataObjectTypeQRCode -> "QR_CODE"
        AVMetadataObjectTypeEAN13Code -> "EAN_13"
        AVMetadataObjectTypeEAN8Code -> "EAN_8"
        AVMetadataObjectTypeCode128Code -> "CODE_128"
        AVMetadataObjectTypeCode39Code -> "CODE_39"
        AVMetadataObjectTypeCode93Code -> "CODE_93"
        AVMetadataObjectTypeUPCECode -> "UPC_E"
        AVMetadataObjectTypePDF417Code -> "PDF417"
        AVMetadataObjectTypeAztecCode -> "AZTEC"
        AVMetadataObjectTypeDataMatrixCode -> "DATA_MATRIX"
        AVMetadataObjectTypeITF14Code -> "ITF"
        else -> "UNKNOWN"
    }
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private class MetadataDelegate(
    private val onDetected: (value: String, type: String) -> Unit
) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: platform.AVFoundation.AVCaptureConnection
    ) {
        for (obj in didOutputMetadataObjects) {
            val readableCode = obj as? AVMetadataMachineReadableCodeObject ?: continue
            val value = readableCode.stringValue ?: continue
            onDetected(value, readableCode.type ?: "UNKNOWN")
            break // Process first valid barcode only
        }
    }
}
