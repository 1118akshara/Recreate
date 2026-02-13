package com.btech.recreate.network

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

object BackendApi {

    private val client = OkHttpClient()

    /* ==========================================================
       1️⃣ ANALYZE SESSION (YOLO + EfficientNet)
     ========================================================== */

    fun uploadMultipleObjects(
        objectImageMap: Map<String, Map<String, File>>,
        onResult: (String?) -> Unit
    ) {

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        for ((objectId, views) in objectImageMap) {
            for ((viewType, file) in views) {

                multipart.addFormDataPart("object_ids", objectId)
                multipart.addFormDataPart("view_types", viewType)

                val body = file
                    .asRequestBody("image/jpeg".toMediaType())

                multipart.addFormDataPart(
                    "files",
                    file.name,
                    body
                )
            }
        }

        val request = Request.Builder()
            .url("${ServerConfig.BASE_URL}/analyze-session")
            .post(multipart.build())
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                onResult(response.body?.string())
            }
        })
    }

    /* ==========================================================
       2️⃣ GENERATE AI REUSE IDEAS
     ========================================================== */

    fun generateAIReuseIdeas(
        analysisJson: String,
        onResult: (String?) -> Unit
    ) {

        val mediaType = "application/json".toMediaType()

        val body = RequestBody.create(
            mediaType,
            analysisJson
        )

        val request = Request.Builder()
            .url("${ServerConfig.BASE_URL}/generate-reuse-ai")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                onResult(response.body?.string())
            }
        })
    }

    /* ==========================================================
       3️⃣ GENERATE STEPS
     ========================================================== */

    fun generateSteps(
        ideaTitle: String,
        onResult: (String?) -> Unit
    ) {

        val json = JSONObject()
        json.put("idea", ideaTitle)

        val body = RequestBody.create(
            "application/json".toMediaType(),
            json.toString()
        )

        val request = Request.Builder()
            .url("${ServerConfig.BASE_URL}/generate-steps")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                onResult(response.body?.string())
            }
        })
    }
}
