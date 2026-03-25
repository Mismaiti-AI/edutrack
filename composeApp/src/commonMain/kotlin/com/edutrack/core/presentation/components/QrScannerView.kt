package com.edutrack.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.edutrack.core.presentation.media.QrScanner
import com.edutrack.core.presentation.media.QrScanResult

/**
 * Pre-built QR/barcode scanner view with viewfinder overlay and dismiss button.
 *
 * Usage:
 * ```
 * QrScannerView(
 *     onScanned = { qrValue -> handleScan(qrValue) },
 *     onDismiss = { showScanner = false }
 * )
 * ```
 */
@Composable
fun QrScannerView(
    onScanned: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    instructionText: String = "Point camera at QR code",
    onError: (String) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        QrScanner(
            modifier = Modifier.fillMaxSize(),
            onScanned = { result: QrScanResult -> onScanned(result.rawValue) },
            onError = onError,
            overlay = {
                // Viewfinder overlay
                Box(modifier = Modifier.fillMaxSize()) {
                    // Semi-transparent scan region indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(250.dp)
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )

                    // Instruction text below viewfinder
                    Text(
                        text = instructionText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 320.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
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
                contentDescription = "Close scanner",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
