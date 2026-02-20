package com.btech.recreate.activities

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import org.json.JSONObject

class AnalysisResultActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_result2)

        val rootLayout = findViewById<LinearLayout>(R.id.resultRootLayout)

        val jsonString = intent.getStringExtra("analysis_result")

        if (jsonString == null) {
            showMessage(rootLayout, "No analysis data received")
            return
        }

        try {

            val mainObject = JSONObject(jsonString)

            if (!mainObject.getBoolean("success")) {
                showMessage(rootLayout, "Backend returned failure")
                return
            }

            val objectsArray = mainObject.getJSONArray("objects")

            for (i in 0 until objectsArray.length()) {

                val obj = objectsArray.getJSONObject(i)

                val detectedObject = obj.optString("object", "Unknown")
                val material = obj.optString("material", "Unknown")
                val confidence = obj.optDouble("confidence", 0.0)
                val objectType = obj.optString("object_type", "Unknown")

                val dimensions = obj.getJSONObject("dimensions")

                val resultText = TextView(this)
                resultText.textSize = 16f
                resultText.setPadding(0, 16, 0, 32)

                val builder = StringBuilder()

                builder.append("Object ${i + 1}\n\n")
                builder.append("Detected Object: $detectedObject\n")
                builder.append("Material: $material\n")
                builder.append("Confidence: %.2f\n".format(confidence))
                builder.append("Object Type: $objectType\n\n")
                builder.append("Dimensions:\n")

                val keys = dimensions.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    builder.append("$key : ${dimensions.getString(key)} cm\n")
                }

                builder.append("\n-----------------------------\n")

                resultText.text = builder.toString()

                rootLayout.addView(resultText)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(rootLayout, "Error parsing analysis result")
        }
    }

    private fun showMessage(layout: LinearLayout, message: String) {
        val textView = TextView(this)
        textView.textSize = 18f
        textView.text = message
        layout.addView(textView)
    }
}