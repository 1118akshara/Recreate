package com.btech.recreate.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.data.ScanRepository
import com.btech.recreate.network.BackendApi
import org.json.JSONArray
import org.json.JSONObject

class AnalysisResultActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val progress = ProgressDialog(this)
        progress.setMessage("Running AI Analysis...")
        progress.setCancelable(false)
        progress.show()

        BackendApi.uploadSession(
            ScanRepository.scannedObjects
        ) { response ->

            runOnUiThread {

                progress.dismiss()

                if (response == null) {
                    Toast.makeText(
                        this,
                        "Backend error",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@runOnUiThread
                }

                buildResultUI(response)
            }
        }
    }

    private fun buildResultUI(response: String) {

        val json = JSONObject(response)

        if (!json.optBoolean("success", false)) {
            Toast.makeText(
                this,
                "Analysis failed",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        val objects = json.optJSONArray("objects") ?: JSONArray()

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }
        scroll.addView(root)

        for (i in 0 until objects.length()) {

            val obj = objects.getJSONObject(i)

            val name = obj.optString("object", "Unknown")
            val material = obj.optString("material", "Unknown")
            val confidence = obj.optDouble("confidence", 0.0)
            val objectType = obj.optString("object_type", "CUBOIDAL")

            val dimensions = obj.optJSONObject("dimensions")

            val height = dimensions?.optString("height_cm", "0")
            val width = dimensions?.optString("width_cm", "0")
            val depth = dimensions?.optString("depth_cm", "0")
            val diameter = dimensions?.optString("diameter_cm", "0")

            val card = TextView(this).apply {

                val dimensionText = if (objectType == "CYLINDRICAL") {
                    """
Height: $height cm
Diameter: $diameter cm
                    """.trimIndent()
                } else {
                    """
Height: $height cm
Width: $width cm
Depth: $depth cm
                    """.trimIndent()
                }

                text = """
Object ${i + 1}

Detected Type: $name
Material: $material
Confidence: ${"%.2f".format(confidence)}

$dimensionText
                """.trimIndent()

                textSize = 16f
                setPadding(0, 0, 0, 50)
            }

            root.addView(card)
        }

        // 🔥 Generate Reuse Ideas Button
        val reuseButton = Button(this).apply {
            text = "Generate Reuse Ideas"
            setOnClickListener {

                BackendApi.generateAIReuseIdeas(response) { reuseResponse ->
                    runOnUiThread {

                        if (reuseResponse != null) {
                            val intent = Intent(
                                this@AnalysisResultActivity2,
                                AIReuseSuggestionsActivity::class.java
                            )
                            intent.putExtra("reuse_json", reuseResponse)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@AnalysisResultActivity2,
                                "Reuse AI failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        root.addView(reuseButton)

        // 🔥 Finish Button
        val finishButton = Button(this).apply {
            text = "Finish Scan"
            setOnClickListener {
                ScanRepository.scannedObjects.clear()
                startActivity(Intent(this@AnalysisResultActivity2, HomeActivity::class.java))
                finish()
            }
        }

        root.addView(finishButton)

        setContentView(scroll)
    }
}
