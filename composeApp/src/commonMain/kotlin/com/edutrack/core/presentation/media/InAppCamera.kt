package com.edutrack.core.presentation.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.edutrack.core.data.media.MediaResult

/**
 * In-app camera composable with live preview and capture button.
 *
 * Displays a camera viewfinder within the app (no external camera app launch).
 * Includes built-in capture button at bottom center and camera switch button at top right.
 *
 * @param modifier Layout modifier for the camera container.
 * @param facing Initial camera facing (default: BACK).
 * @param flashMode Flash mode (default: AUTO).
 * @param onCapture Called with the captured photo as [MediaResult].
 * @param onError Called if camera initialization or capture fails.
 */
@Composable
expect fun InAppCamera(
    modifier: Modifier = Modifier,
    facing: CameraFacing = CameraFacing.BACK,
    flashMode: FlashMode = FlashMode.AUTO,
    onCapture: (MediaResult) -> Unit,
    onError: (String) -> Unit = {},
)
