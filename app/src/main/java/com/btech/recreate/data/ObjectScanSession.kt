package com.btech.recreate.data

data class ObjectScanSession(
    val id: String,

    var heightCm: Float = 0f,
    var widthCm: Float = 0f,
    var depthCm: Float = 0f,

    var imagePath: String = "",

    // 🔥 NEW FIELD
    var objectType: String = "CUBOIDAL"
)

