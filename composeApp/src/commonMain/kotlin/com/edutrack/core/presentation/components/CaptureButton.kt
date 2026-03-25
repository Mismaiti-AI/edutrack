package com.edutrack.core.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.edutrack.core.data.media.MediaPickerType
import com.edutrack.core.data.media.MediaResult
import com.edutrack.core.presentation.media.CameraCaptureMode
import com.edutrack.core.presentation.media.CameraFacing
import com.edutrack.core.presentation.media.InAppCamera
import com.edutrack.core.presentation.media.rememberCameraLauncher
import com.edutrack.core.presentation.media.rememberMediaPickerLauncher

/**
 * Pre-styled button that opens the device camera and returns captured media as [MediaResult].
 *
 * Usage:
 * ```
 * // Photo only (default)
 * CaptureButton(onCaptured = { photo = it })
 *
 * // Video only
 * CaptureButton(mode = CameraCaptureMode.VIDEO, label = "Record Video", onCaptured = { video = it })
 *
 * // Photo + Video (user can switch in camera UI)
 * CaptureButton(mode = CameraCaptureMode.PHOTO_AND_VIDEO, label = "Capture", onCaptured = { media = it })
 * ```
 */
@Composable
fun CaptureButton(
    onCaptured: (MediaResult?) -> Unit,
    modifier: Modifier = Modifier,
    mode: CameraCaptureMode = CameraCaptureMode.PHOTO,
    label: String = "Take Photo",
    icon: ImageVector = Icons.Default.CameraAlt
) {
    val cameraLauncher = rememberCameraLauncher(mode = mode, onResult = onCaptured)

    Button(
        onClick = { cameraLauncher.launch() },
        modifier = modifier
    ) {
        Icon(imageVector = icon, contentDescription = label)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

/**
 * Pre-styled button that opens the system media/file picker and returns selected item as [MediaResult].
 *
 * Usage:
 * ```
 * var image by remember { mutableStateOf<MediaResult?>(null) }
 * PickMediaButton(type = MediaPickerType.IMAGE, onPicked = { image = it })
 * ```
 */
@Composable
fun PickMediaButton(
    onPicked: (MediaResult?) -> Unit,
    modifier: Modifier = Modifier,
    type: MediaPickerType = MediaPickerType.IMAGE,
    label: String = "Pick File",
    icon: ImageVector = Icons.Default.AttachFile
) {
    val pickerLauncher = rememberMediaPickerLauncher(type = type, onResult = onPicked)

    Button(
        onClick = { pickerLauncher.launch() },
        modifier = modifier
    ) {
        Icon(imageVector = icon, contentDescription = label)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

/**
 * Full-screen in-app camera view with live preview, capture button, and close button.
 *
 * Usage:
 * ```
 * if (showCamera) {
 *     InAppCaptureView(
 *         onCaptured = { photo -> handlePhoto(photo) },
 *         onDismiss = { showCamera = false }
 *     )
 * }
 * ```
 */
@Composable
fun InAppCaptureView(
    onCaptured: (MediaResult) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    facing: CameraFacing = CameraFacing.BACK,
    onError: (String) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        InAppCamera(
            modifier = Modifier.fillMaxSize(),
            facing = facing,
            onCapture = onCaptured,
            onError = onError,
        )

        // Close button — top left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                .clip(CircleShape)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close camera",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
