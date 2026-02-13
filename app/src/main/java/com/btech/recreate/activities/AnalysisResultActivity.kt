package com.btech.recreate.activities

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class AnalysisResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        // 🔹 TEMP: simulate backend response
        val fakeResponse = JSONObject(
            """
            {
              "results": [
                {
                  "object_id": "object_1",
                  "label": "Bottle",
                  "material": "Plastic",
                  "dimensions": {
                    "height_cm": 28.5,
                    "width_cm": 7.2
                  }
                }
              ]
            }
            """
        )

        val results = fakeResponse.getJSONArray("results")

        for (i in 0 until results.length()) {
            val obj = results.getJSONObject(i)

            val text = TextView(this).apply {
                text = """
                Object: ${obj.getString("label")}
                Material: ${obj.getString("material")}
                Height: ${obj.getJSONObject("dimensions").getDouble("height_cm")} cm
                Width: ${obj.getJSONObject("dimensions").getDouble("width_cm")} cm
                """.trimIndent()
                textSize = 16f
                setPadding(0, 0, 0, 24)
            }

            root.addView(text)
        }

        setContentView(root)
    }
}