package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.btech.recreate.data.ScanRepository
import com.btech.recreate.network.BackendApi
import java.io.File

class ScanSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root ScrollView
        val scrollView = ScrollView(this)

        // Root layout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        scrollView.addView(rootLayout)

        // Title
        val title = TextView(this).apply {
            text = "Scan Summary"
            textSize = 22f
            setPadding(0, 0, 0, 24)
        }
        rootLayout.addView(title)

        // Object cards
        ScanRepository.scannedObjects.forEachIndexed { index, obj ->

            val card = CardView(this).apply {
                radius = 20f
                cardElevation = 8f
                useCompatPadding = true
                setContentPadding(24, 24, 24, 24)
            }

            val content = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            val header = TextView(this).apply {
                text = "Object ${index + 1}"
                textSize = 18f
                setPadding(0, 0, 0, 12)
            }
            content.addView(header)

            obj.captures.values.forEach { frame ->
                val viewText = TextView(this).apply {
                    text = "• ${frame.viewType.displayName}"
                    textSize = 15f
                    setPadding(0, 6, 0, 6)
                    setOnClickListener {
                        val intent = Intent(
                            this@ScanSummaryActivity,
                            ImagePreviewActivity::class.java
                        )
                        intent.putExtra("objectIndex", index)
                        intent.putExtra("viewType", frame.viewType.name)
                        startActivity(intent)
                    }
                }
                content.addView(viewText)
            }

            card.addView(content)
            rootLayout.addView(card)

            // spacing
            val spacer = TextView(this)
            spacer.height = 24
            rootLayout.addView(spacer)
        }

        // Analyze button
        val analyzeButton = Button(this).apply {
            text = "Analyze Objects (AI)"
            textSize = 16f
            setOnClickListener {

                val imageDir = File(
                    getExternalFilesDir(null),
                    "Pictures/recreate_scans"
                )

                if (!imageDir.exists()) {
                    Toast.makeText(
                        this@ScanSummaryActivity,
                        "No images found",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val imageFiles = imageDir
                    .listFiles { file -> file.extension.lowercase() == "jpg" }
                    ?.toList()
                    ?: emptyList()

                Log.d("RECREATE", "Found ${imageFiles.size} images")

                if (imageFiles.isEmpty()) {
                    Toast.makeText(
                        this@ScanSummaryActivity,
                        "No images to analyze",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                BackendApi.uploadImages(imageFiles) { response ->
                    runOnUiThread {
                        if (response != null && response.trim().startsWith("{")) {
                            val intent = Intent(
                                this@ScanSummaryActivity,
                                AnalysisResultActivity2::class.java
                            )
                            intent.putExtra("analysis_json", response)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@ScanSummaryActivity,
                                "Analysis failed. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        rootLayout.addView(analyzeButton)

        setContentView(scrollView)
    }
}
