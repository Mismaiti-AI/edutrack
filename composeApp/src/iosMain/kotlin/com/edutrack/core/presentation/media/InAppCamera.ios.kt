package com.edutrack.core.presentation.media

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.edutrack.core.data.media.MediaResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetPhoto
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.defaultDeviceWithDeviceType
import platform.AVFoundation.fileDataRepresentation
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun InAppCamera(
    modifier: Modifier,
    facing: CameraFacing,
    flashMode: FlashMode,
    onCapture: (MediaResult) -> Unit,
    onError: (String) -> Unit,
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

    var currentFacing by remember { mutableStateOf(facing) }
    val cameraManager = remember { IosCameraManager() }

    // Handle camera switch via LaunchedEffect (UIKitView has no update parameter)
    LaunchedEffect(currentFacing) {
        cameraManager.switchCamera(currentFacing.toAVPosition())
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopSession()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        UIKitView(
            factory = {
                val containerView = UIView()
                cameraManager.setupSession(
                    view = containerView,
                    position = currentFacing.toAVPosition()
                )
                containerView
            },
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
                    cameraManager.capturePhoto { data ->
                        if (data != null) {
                            val bytes = iosNsDataToByteArray(data)
                            onCapture(
                                MediaResult(
                                    bytes = bytes,
                                    fileName = "in_app_photo_${NSDate().timeIntervalSince1970.toLong()}.jpg",
                                    mimeType = "image/jpeg",
                                    size = bytes.size.toLong()
                                )
                            )
                        } else {
                            onError("Failed to capture photo")
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Empty white circle is the capture button visual
        }
    }
}

private fun CameraFacing.toAVPosition(): Long = when (this) {
    CameraFacing.BACK -> AVCaptureDevicePositionBack
    CameraFacing.FRONT -> AVCaptureDevicePositionFront
}

/** Convert NSData to ByteArray (local copy to avoid internal access issues). */
@OptIn(ExperimentalForeignApi::class)
private fun iosNsDataToByteArray(data: NSData): ByteArray {
    val size = data.length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        memcpy(bytes.refTo(0), data.bytes, data.length)
    }
    return bytes
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class IosCameraManager {
    private var session: AVCaptureSession? = null
    private var photoOutput: AVCapturePhotoOutput? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var currentPosition: Long = AVCaptureDevicePositionBack
    private var captureDelegate: PhotoCaptureDelegate? = null

    fun setupSession(view: UIView, position: Long) {
        currentPosition = position
        val captureSession = AVCaptureSession()
        captureSession.sessionPreset = AVCaptureSessionPresetPhoto

        val device = AVCaptureDevice.defaultDeviceWithDeviceType(
            deviceType = AVCaptureDeviceTypeBuiltInWideAngleCamera,
            mediaType = AVMediaTypeVideo,
            position = position
        ) ?: return

        val input = try {
            AVCaptureDeviceInput(device = device, error = null)
        } catch (_: Exception) {
            return
        }

        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        val output = AVCapturePhotoOutput()
        if (captureSession.canAddOutput(output)) {
            captureSession.addOutput(output)
        }

        val layer = AVCaptureVideoPreviewLayer(session = captureSession)
        layer.videoGravity = AVLayerVideoGravityResizeAspectFill
        layer.frame = view.bounds
        view.layer.addSublayer(layer)

        session = captureSession
        photoOutput = output
        previewLayer = layer

        captureSession.startRunning()
    }

    fun switchCamera(position: Long) {
        if (position == currentPosition) return
        val captureSession = session ?: return
        currentPosition = position

        // Remove existing input
        captureSession.inputs.forEach { input ->
            captureSession.removeInput(input as platform.AVFoundation.AVCaptureInput)
        }

        val device = AVCaptureDevice.defaultDeviceWithDeviceType(
            deviceType = AVCaptureDeviceTypeBuiltInWideAngleCamera,
            mediaType = AVMediaTypeVideo,
            position = position
        ) ?: return

        val input = try {
            AVCaptureDeviceInput(device = device, error = null)
        } catch (_: Exception) {
            return
        }

        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }
    }

    fun capturePhoto(completion: (NSData?) -> Unit) {
        val output = photoOutput ?: run {
            completion(null)
            return
        }
        val settings = AVCapturePhotoSettings()
        val delegate: PhotoCaptureDelegate = PhotoCaptureDelegate { data ->
            captureDelegate = null // Clear strong ref after capture
            completion(data)
        }
        captureDelegate = delegate // Strong reference to prevent GC
        output.capturePhotoWithSettings(settings, delegate = delegate)
    }

    fun stopSession() {
        session?.stopRunning()
        session = null
        photoOutput = null
        previewLayer?.removeFromSuperlayer()
        previewLayer = null
        captureDelegate = null
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class PhotoCaptureDelegate(
    private val completion: (NSData?) -> Unit
) : NSObject(), AVCapturePhotoCaptureDelegateProtocol {

    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: platform.Foundation.NSError?
    ) {
        if (error != null) {
            dispatch_async(dispatch_get_main_queue()) { completion(null) }
            return
        }
        val imageData = didFinishProcessingPhoto.fileDataRepresentation()
        dispatch_async(dispatch_get_main_queue()) { completion(imageData) }
    }
}
