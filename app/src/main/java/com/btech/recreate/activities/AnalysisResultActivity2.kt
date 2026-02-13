package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import org.json.JSONObject

class AnalysisResultActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_result2)

        val txtObjects = findViewById<TextView>(R.id.txtObjects)
        val txtReuse = findViewById<TextView>(R.id.txtReuse)
        val btnAIReuse = findViewById<Button>(R.id.btnAIReuse)

        val jsonString = intent.getStringExtra("analysis_json")

        if (jsonString.isNullOrBlank()) {
            txtObjects.text = "No analysis data received."
            txtReuse.text = ""
            return
        }

        try {
            val json = JSONObject(jsonString)

            val objectsArray = json.optJSONArray("objects")
            val combinedReuseArray = json.optJSONArray("combined_reuse_suggestions")

            if (objectsArray == null || objectsArray.length() == 0) {
                txtObjects.text = "No objects detected."
                txtReuse.text = ""
                return
            }

            val objectsText = StringBuilder()
            objectsText.append("Detected Objects\n\n")

            for (i in 0 until objectsArray.length()) {
                val obj = objectsArray.getJSONObject(i)

                val name = obj.optString("object", "Unknown")
                val material = obj.optString("material", "Unknown")

                val height = obj
                    .optJSONObject("dimensions")
                    ?.optDouble("height_cm", 0.0)
                    ?: 0.0

                objectsText.append("• Object ${i + 1}\n")
                objectsText.append("  Type: $name\n")
                objectsText.append("  Material: $material\n")
                objectsText.append("  Height: ${"%.2f".format(height)} cm\n\n")
            }

            txtObjects.text = objectsText.toString()

            val reuseText = StringBuilder()

            if (combinedReuseArray != null && combinedReuseArray.length() > 0) {
                reuseText.append("Combined Reuse Suggestions\n\n")
                for (i in 0 until combinedReuseArray.length()) {
                    reuseText.append("• ${combinedReuseArray.getString(i)}\n")
                }
            } else {
                reuseText.append("No combined reuse suggestions available.")
            }

            txtReuse.text = reuseText.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            txtObjects.text = "Failed to parse analysis result."
            txtReuse.text = ""
        }

        // NAVIGATION TO REUSE SUGGESTIONS PAGE
        btnAIReuse.setOnClickListener {

            val intent = Intent(
                this@AnalysisResultActivity2,
                AIReuseSuggestionsActivity::class.java

            )
            intent.putExtra("analysis_json", jsonString)
            startActivity(intent)
        }
    }
}
