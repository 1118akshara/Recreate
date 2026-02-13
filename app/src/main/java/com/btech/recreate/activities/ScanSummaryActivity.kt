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
import org.json.JSONObject
import java.io.File

class ScanSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---------- UI ROOT ----------
        val scrollView = ScrollView(this)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        scrollView.addView(rootLayout)

        // ---------- TITLE ----------
        val title = TextView(this).apply {
            text = "Scan Summary"
            textSize = 22f
            setPadding(0, 0, 0, 24)
        }
        rootLayout.addView(title)

        // ---------- OBJECT CARDS ----------
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

            val spacer = TextView(this)
            spacer.height = 24
            rootLayout.addView(spacer)
        }

        // ---------- ANALYZE BUTTON ----------
        val analyzeButton = Button(this).apply {
            text = "Analyze Objects (AI)"
            textSize = 16f

            setOnClickListener {

                // Build object → view → file map
                val objectImageMap = mutableMapOf<String, Map<String, File>>()

                ScanRepository.scannedObjects.forEachIndexed { index, obj ->
                    val viewMap = mutableMapOf<String, File>()

                    obj.captures.forEach { (viewType, frame) ->
                        val file = File(frame.imagePath)
                        if (file.exists()) {
                            viewMap[viewType.name] = file
                            Log.d(
                                "UPLOAD_DEBUG",
                                "object_${index + 1} -> ${viewType.name} -> ${file.absolutePath}"
                            )
                        }
                    }

                    if (viewMap.isNotEmpty()) {
                        objectImageMap["object_${index + 1}"] = viewMap
                    }
                }

                if (objectImageMap.isEmpty()) {
                    Toast.makeText(
                        this@ScanSummaryActivity,
                        "No images to analyze",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // ---------- BACKEND CALL ----------
                BackendApi.uploadMultipleObjects(objectImageMap) { response ->
                    runOnUiThread {

                        if (response == null) {
                            Toast.makeText(
                                this@ScanSummaryActivity,
                                "No response from server",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@runOnUiThread
                        }

                        try {
                            val json = JSONObject(response)

                            if (!json.has("objects")) {
                                Toast.makeText(
                                    this@ScanSummaryActivity,
                                    "Invalid analysis result",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@runOnUiThread
                            }

                            val intent = Intent(
                                this@ScanSummaryActivity,
                                AnalysisResultActivity2::class.java
                            )
                            intent.putExtra(
                                "analysis_json",
                                json.toString()
                            )
                            startActivity(intent)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@ScanSummaryActivity,
                                "Failed to parse analysis results",
                                Toast.LENGTH_LONG
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
