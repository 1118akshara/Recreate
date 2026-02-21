package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
        val btnReuse = findViewById<Button>(R.id.btnReuse)

        val jsonString = intent.getStringExtra("analysis_result")

        if (jsonString.isNullOrEmpty()) {
            showMessage(rootLayout, "No analysis data received")
            return
        }

        try {

            val mainObject = JSONObject(jsonString)

            if (!mainObject.optBoolean("success", false)) {
                showMessage(rootLayout, "Backend returned failure")
                return
            }

            val objectsArray = mainObject.getJSONArray("objects")

            if (objectsArray.length() == 0) {
                showMessage(rootLayout, "No objects detected")
                return
            }

            // 🔥 LOOP THROUGH ALL ANALYZED OBJECTS
            for (i in 0 until objectsArray.length()) {

                val obj = objectsArray.getJSONObject(i)

                val detectedObject = obj.optString("object", "Unknown")
                val material = obj.optString("material", "Unknown")
                val confidence = obj.optDouble("confidence", 0.0)
                val objectType = obj.optString("object_type", "Unknown")
                val dimensions = obj.optJSONObject("dimensions")

                // =========================
                // CARD CONTAINER
                // =========================
                val container = LinearLayout(this)
                container.orientation = LinearLayout.VERTICAL
                container.setPadding(40, 40, 40, 40)
                container.setBackgroundColor(0xFFFFFFFF.toInt())

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 50)
                container.layoutParams = params

                // =========================
                // TEXT CONTENT
                // =========================
                val builder = StringBuilder()

                builder.append("Object ${i + 1}\n\n")
                builder.append("Detected Object: $detectedObject\n")
                builder.append("Material: $material\n")
                builder.append("Confidence: %.2f\n".format(confidence))
                builder.append("Object Type: $objectType\n\n")
                builder.append("Dimensions:\n")

                if (dimensions != null) {
                    val keys = dimensions.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        builder.append("$key : ${dimensions.getString(key)} cm\n")
                    }
                }

                val resultText = TextView(this)
                resultText.textSize = 16f
                resultText.setTextColor(0xFF1B5E20.toInt()) // dark green text
                resultText.text = builder.toString()

                container.addView(resultText)

                // Add card to layout
                rootLayout.addView(container)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(rootLayout, "Error parsing analysis result")
        }

        // Optional: Reuse button click
        btnReuse.setOnClickListener {
            val intent = Intent(this, AIReuseSuggestionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showMessage(layout: LinearLayout, message: String) {
        val textView = TextView(this)
        textView.textSize = 18f
        textView.setTextColor(0xFFD32F2F.toInt()) // red for error
        textView.text = message
        layout.addView(textView)
    }
}