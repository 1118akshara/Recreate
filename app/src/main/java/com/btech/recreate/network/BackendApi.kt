package com.btech.recreate.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

object BackendApi {

    // For Android Emulator
    private const val SERVER_URL =
        "http://192.168.20.12:8000/analyze-session"


    private val client = OkHttpClient()

    fun uploadImages(
        imageFiles: List<File>,
        onResult: (String?) -> Unit
    ) {
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        for (file in imageFiles) {
            val requestBody = file.asRequestBody("image/jpeg".toMediaType())
            multipartBody.addFormDataPart(
                "files",
                file.name,
                requestBody
            )
        }

        val request = Request.Builder()
            .url(SERVER_URL)
            .post(multipartBody.build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult("ERROR: ${e.message}")
            }


            override fun onResponse(call: Call, response: Response) {
                onResult(response.body?.string())
            }
        })
    }
}
