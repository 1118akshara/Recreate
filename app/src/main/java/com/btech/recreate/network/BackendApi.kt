package com.btech.recreate.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import com.btech.recreate.data.ObjectScanSession

object BackendApi {

    private val client = OkHttpClient()

    /* ==========================================================
       1️⃣ UPLOAD SESSION (YOLO + EfficientNet)
     ========================================================== */

    fun uploadSession(
        objects: List<ObjectScanSession>,
        onResult: (String?) -> Unit
    ) {

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        objects.forEachIndexed { index, obj ->

            val file = File(obj.imagePath ?: return@forEachIndexed)
            if (!file.exists()) return@forEachIndexed

            val body =
                file.asRequestBody("image/jpeg".toMediaType())

            // IMPORTANT: must match FastAPI
            multipart.addFormDataPart(
                "files",
                file.name,
                body
            )

            multipart.addFormDataPart(
                "height_cm_$index",
                obj.heightCm?.toString() ?: ""
            )

            multipart.addFormDataPart(
                "width_cm_$index",
                obj.widthCm?.toString() ?: ""
            )

            multipart.addFormDataPart(
                "depth_cm_$index",
                obj.depthCm?.toString() ?: ""
            )
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

        val body = RequestBody.create(
            "application/json".toMediaType(),
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
