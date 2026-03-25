package com.edutrack.core.presentation.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * QR/barcode scanner composable with live viewfinder.
 *
 * Displays a camera preview with real-time frame analysis for QR codes and barcodes.
 * Fires [onScanned] once per unique detected value (deduplicated until composable is removed).
 *
 * @param modifier Layout modifier for the scanner container.
 * @param onScanned Called when a QR/barcode is detected with the decoded result.
 * @param onError Called if camera initialization fails.
 * @param overlay Optional composable overlay (e.g., scan region indicator, instruction text).
 */
@Composable
expect fun QrScanner(
    modifier: Modifier = Modifier,
    onScanned: (QrScanResult) -> Unit,
    onError: (String) -> Unit = {},
    overlay: @Composable (() -> Unit)? = null,
)
