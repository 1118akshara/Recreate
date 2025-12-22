package com.btech.recreate.analysis

import com.btech.recreate.data.ScanRepository
import org.json.JSONArray
import org.json.JSONObject

object AnalysisPayloadBuilder {

    fun buildPayload(): JSONObject {
        val root = JSONObject()
        val objectsArray = JSONArray()

        ScanRepository.scannedObjects.forEachIndexed { index, obj ->
            val objJson = JSONObject()
            objJson.put("object_id", "object_${index + 1}")

            val imagesArray = JSONArray()

            obj.captures.values.forEach { frame ->
                val imageJson = JSONObject()
                imageJson.put("view", frame.viewType.name)
                imageJson.put("image_path", frame.imagePath)
                imageJson.put("pose", JSONArray(frame.poseMatrix.toList()))
                imagesArray.put(imageJson)
            }

            objJson.put("images", imagesArray)
            objectsArray.put(objJson)
        }

        root.put("objects", objectsArray)
        return root
    }
}