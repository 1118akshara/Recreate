package com.btech.recreate.data

data class ObjectScanSession(
    val objectId: String,
    val captures: MutableMap<ViewType, CaptureFrame> = mutableMapOf()
)