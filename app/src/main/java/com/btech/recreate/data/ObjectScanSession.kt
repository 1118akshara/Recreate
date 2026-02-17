package com.btech.recreate.data

data class ObjectScanSession(
    val objectId: String,
    var imagePath: String? = null,
    var heightCm: Float? = null,
    var widthCm: Float? = null,
    var depthCm: Float? = null
)
