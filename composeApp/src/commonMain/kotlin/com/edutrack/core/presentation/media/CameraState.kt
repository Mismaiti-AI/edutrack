package com.edutrack.core.presentation.media

/** Which camera lens to use. */
enum class CameraFacing { BACK, FRONT }

/** Flash mode for photo capture. */
enum class FlashMode { AUTO, ON, OFF }

/** Result from QR/barcode scanning. */
data class QrScanResult(
    /** The decoded content of the QR code or barcode. */
    val rawValue: String,
    /** The barcode format (e.g., "QR_CODE", "EAN_13", "CODE_128"). */
    val format: String
)
