package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.network.BackendApi
import org.json.JSONArray
import org.json.JSONObject

class AnalysisResultActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val result = intent.getStringExtra("analysis_json") ?: return
        val json = JSONObject(result)
        val objects = json.optJSONArray("objects") ?: JSONArray()

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        scroll.addView(root)

        for (i in 0 until objects.length()) {

            val obj = objects.getJSONObject(i)

            val text = TextView(this).apply {
                text = """
Object ${i + 1}

Name: ${obj.optString("object")}
Material: ${obj.optString("material")}
Confidence: ${"%.2f".format(obj.optDouble("confidence"))}

Height: ${obj.getJSONObject("dimensions").optDouble("height_cm")} cm
Width: ${obj.getJSONObject("dimensions").optDouble("width_cm")} cm
Depth: ${obj.getJSONObject("dimensions").optDouble("depth_cm")} cm
                """.trimIndent()
                textSize = 16f
                setPadding(0, 0, 0, 24)
            }

            root.addView(text)
        }

        val reuseButton = Button(this).apply {
            text = "Generate Reuse Ideas"
            setOnClickListener {

                BackendApi.generateAIReuseIdeas(result) { reuseResponse ->
                    runOnUiThread {
                        if (reuseResponse != null) {
                            val intent = Intent(
                                this@AnalysisResultActivity2,
                                AIReuseSuggestionsActivity::class.java
                            )
                            intent.putExtra("reuse_json", reuseResponse)
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        root.addView(reuseButton)

        setContentView(scroll)
    }
}
