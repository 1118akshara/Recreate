package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.btech.recreate.data.ScanRepository
import com.btech.recreate.network.BackendApi
import org.json.JSONObject

class ScanSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        scrollView.addView(rootLayout)

        val title = TextView(this).apply {
            text = "Scan Summary"
            textSize = 22f
        }
        rootLayout.addView(title)

        // 🔹 Display objects
        ScanRepository.scannedObjects.forEachIndexed { index, obj ->

            val card = CardView(this).apply {
                radius = 24f
                cardElevation = 12f
                setContentPadding(32, 32, 32, 32)
            }

            val content = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            val header = TextView(this).apply {
                text = "Object ${index + 1}"
                textSize = 18f
            }

            val dimensionText = TextView(this).apply {
                text = """
Height: ${"%.2f".format(obj.heightCm ?: 0f)} cm
Width: ${"%.2f".format(obj.widthCm ?: 0f)} cm
Depth: ${"%.2f".format(obj.depthCm ?: 0f)} cm
                """.trimIndent()
            }

            content.addView(header)
            content.addView(dimensionText)
            card.addView(content)
            rootLayout.addView(card)
        }

        // 🔹 Loading status text
        val loadingText = TextView(this).apply {
            text = ""
            textSize = 16f
            setPadding(0, 24, 0, 24)
        }
        rootLayout.addView(loadingText)

        val analyzeButton = Button(this).apply {
            text = "Analyze Objects (AI)"
            setOnClickListener {

                if (ScanRepository.scannedObjects.isEmpty()) {
                    Toast.makeText(
                        this@ScanSummaryActivity,
                        "No objects to analyze",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                loadingText.text = "Running YOLO + EfficientNet..."

                BackendApi.uploadSession(
                    ScanRepository.scannedObjects
                ) { response ->

                    runOnUiThread {

                        if (response == null) {
                            loadingText.text = ""
                            Toast.makeText(
                                this@ScanSummaryActivity,
                                "Server connection failed",
                                Toast.LENGTH_LONG
                            ).show()
                            return@runOnUiThread
                        }

                        Log.d("AI_RESPONSE", response)

                        val json = JSONObject(response)

                        if (!json.has("objects")) {
                            loadingText.text = ""
                            Toast.makeText(
                                this@ScanSummaryActivity,
                                "AI returned invalid result",
                                Toast.LENGTH_LONG
                            ).show()
                            return@runOnUiThread
                        }

                        loadingText.text = "AI Analysis Complete"

                        val intent = Intent(
                            this@ScanSummaryActivity,
                            AnalysisResultActivity2::class.java
                        )
                        intent.putExtra("analysis_json", json.toString())
                        startActivity(intent)
                    }
                }
            }
        }

        rootLayout.addView(analyzeButton)
        setContentView(scrollView)
    }
}
