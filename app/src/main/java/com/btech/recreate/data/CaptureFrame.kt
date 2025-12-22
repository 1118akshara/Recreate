package com.btech.recreate.data

data class CaptureFrame(
    val viewType: ViewType,
    var imagePath: String,
    val poseMatrix: FloatArray,
    val timestamp: Long
)