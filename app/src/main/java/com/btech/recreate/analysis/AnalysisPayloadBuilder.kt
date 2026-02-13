package com.btech.recreate.analysis

import com.btech.recreate.data.ObjectScanSession
import org.json.JSONArray
import org.json.JSONObject

object AnalysisPayloadBuilder {

    fun buildPayload(objects: List<ObjectScanSession>): String {

        val objectsArray = JSONArray()

        objects.forEach { obj ->

            val objectJson = JSONObject()

            objectJson.put("height_cm", obj.heightCm ?: 0f)
            objectJson.put("width_cm", obj.widthCm ?: 0f)
            objectJson.put("depth_cm", obj.depthCm ?: 0f)

            objectJson.put("image_path", obj.imagePath ?: "")

            objectsArray.put(objectJson)
        }

        val root = JSONObject()
        root.put("objects", objectsArray)

        return root.toString()
    }
}
