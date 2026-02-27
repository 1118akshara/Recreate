package com.btech.recreate.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import com.btech.recreate.data.ObjectScanSession
import org.json.JSONArray
object BackendApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    /* ==========================================================
       1️⃣ UPLOAD SESSION (YOLO + EfficientNet)
     ========================================================== */

    fun uploadSession(
        objects: List<ObjectScanSession>,
        onResult: (String?) -> Unit
    ) {

        println("🔥 UPLOAD SESSION CALLED")
        println("Objects: $objects")
        println("Objects size: ${objects.size}")


        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        objects.forEachIndexed { index, obj ->

            if (obj.imagePath.isNullOrEmpty()) return@forEachIndexed

            val file = File(obj.imagePath!!)
            if (!file.exists()) return@forEachIndexed

            val imageBody =
                file.asRequestBody("image/jpeg".toMediaType())

            // 🔥 Image file
            multipart.addFormDataPart(
                "files",
                file.name,
                imageBody
            )

            // 🔥 Object type
            multipart.addFormDataPart(
                "object_type_$index",
                obj.objectType ?: "CUBOIDAL"
            )

            // 🔥 Height always present
            multipart.addFormDataPart(
                "height_cm_$index",
                obj.heightCm.toString()
            )

            // 🔥 Cylindrical → height + diameter
            if (obj.objectType == "CYLINDRICAL") {

                multipart.addFormDataPart(
                    "diameter_cm_$index",
                    obj.widthCm.toString() // width holds diameter
                )

            } else {

                // Flat / Cuboidal / VeryFlat
                multipart.addFormDataPart(
                    "width_cm_$index",
                    obj.widthCm.toString()
                )

                multipart.addFormDataPart(
                    "depth_cm_$index",
                    obj.depthCm.toString()
                )
            }
        }

        val request = Request.Builder()
            .url("${ServerConfig.BASE_URL}/analyze-session")
            .post(multipart.build())
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onResult(null)
                    return
                }

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

        val original = JSONObject(analysisJson)
        val objects = original.getJSONArray("objects")

        val requestJson = JSONObject()
        requestJson.put("objects", objects)

        val body = requestJson.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${ServerConfig.BASE_URL}/generate-reuse-ai")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {

                if (!response.isSuccessful) {
                    onResult(null)
                    return
                }

                val responseBody = response.body
                if (responseBody == null) {
                    onResult(null)
                    return
                }

                val bodyString = responseBody.string()

                println("AI RESPONSE: $bodyString")

                onResult(bodyString)
            }
        })
    }

    /* ==========================================================
       3️⃣ GENERATE STEPS
     ========================================================== */
    fun generateSteps(
        ideaTitle: String,
        analysisJson: String,
        onResult: (String?) -> Unit
    ) {

        try {

            val original = JSONObject(analysisJson)
            val objects = original.optJSONArray("objects") ?: JSONArray()

            val requestJson = JSONObject()
            requestJson.put("idea_title", ideaTitle)
            requestJson.put("objects", objects)

            val body = requestJson.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ServerConfig.BASE_URL}/generate-steps")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    println("❌ STEP NETWORK FAILURE: ${e.message}")
                    onResult(null)
                }

                override fun onResponse(call: Call, response: Response) {

                    println("✅ STEP RESPONSE CODE: ${response.code}")

                    if (!response.isSuccessful) {
                        println("❌ HTTP ERROR")
                        onResult(null)
                        return
                    }

                    val bodyString = response.body?.string()

                    println("📦 STEP RAW BODY: $bodyString")

                    if (bodyString.isNullOrEmpty()) {
                        println("❌ EMPTY BODY")
                        onResult(null)
                        return
                    }

                    onResult(bodyString)
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ JSON BUILD ERROR: ${e.message}")
            onResult(null)
        }
    }
}

