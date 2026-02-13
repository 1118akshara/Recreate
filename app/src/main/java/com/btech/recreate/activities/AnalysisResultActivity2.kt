package com.btech.recreate.activities

import android.os.Bundle
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

        val jsonString = intent.getStringExtra("analysis_json")

        if (jsonString.isNullOrBlank()) {
            txtObjects.text = "No analysis data received."
            txtReuse.text = ""
            return
        }

        try {
            val json = JSONObject(jsonString)

            val objectsArray = json.getJSONArray("objects")
            val combinedReuseArray = json.getJSONArray("combined_reuse_suggestions")

            /* -----------------------------
               OBJECT DETAILS SECTION
             ----------------------------- */
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
                objectsText.append("  Height: ${"%.2f".format(height)} cm\n")

                // Per-object reuse ideas
                if (obj.has("reuse_ideas")) {
                    val reuseIdeas = obj.getJSONArray("reuse_ideas")
                    if (reuseIdeas.length() > 0) {
                        objectsText.append("  Reuse Ideas:\n")
                        for (j in 0 until reuseIdeas.length()) {
                            objectsText.append("   • ${reuseIdeas.getString(j)}\n")
                        }
                    }
                }

                objectsText.append("\n")
            }

            txtObjects.text = objectsText.toString()

            /* -----------------------------
               COMBINED REUSE SECTION
             ----------------------------- */
            val reuseText = StringBuilder()

            if (combinedReuseArray.length() > 0) {
                reuseText.append("Combined Reuse Suggestions\n\n")
                for (i in 0 until combinedReuseArray.length()) {
                    reuseText.append("• ${combinedReuseArray.getString(i)}\n")
                }
            } else {
                reuseText.append("No combined reuse suggestions available.")
            }

            txtReuse.text = reuseText.toString()

        } catch (e: Exception) {
            txtObjects.text = "Failed to parse analysis result."
            txtReuse.text = ""
            e.printStackTrace()
        }
    }
}
