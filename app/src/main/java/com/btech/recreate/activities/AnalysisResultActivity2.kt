package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.btech.recreate.R
import com.btech.recreate.data.ScanRepository
import org.json.JSONObject
import java.io.File

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

            for (i in 0 until objectsArray.length()) {

                val obj = objectsArray.getJSONObject(i)

                val detectedObject = obj.optString("object", "Unknown")
                val material = obj.optString("material", "Unknown")
                val confidence = obj.optDouble("confidence", 0.0)
                val dimensions = obj.optJSONObject("dimensions")

                // =========================
                // CARD VIEW
                // =========================

                val card = CardView(this)
                card.radius = 25f
                card.cardElevation = 10f
                card.setCardBackgroundColor(0xFFFFFFFF.toInt())

                val cardParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                cardParams.setMargins(0, 0, 0, 30)
                card.layoutParams = cardParams

                val container = LinearLayout(this)
                container.orientation = LinearLayout.HORIZONTAL
                container.setPadding(30, 30, 30, 30)

                // =========================
                // SMALL IMAGE
                // =========================

                val imageView = ImageView(this)
                val imgParams = LinearLayout.LayoutParams(220, 220)
                imgParams.setMargins(0, 0, 30, 0)
                imageView.layoutParams = imgParams
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                val scannedObject = ScanRepository.scannedObjects.getOrNull(i)
                if (scannedObject?.imagePath != null) {
                    val file = File(scannedObject.imagePath!!)
                    if (file.exists()) {
                        imageView.setImageURI(android.net.Uri.fromFile(file))
                    }
                }

                // =========================
                // TEXT COLUMN
                // =========================

                val textColumn = LinearLayout(this)
                textColumn.orientation = LinearLayout.VERTICAL
                textColumn.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

                val objectText = TextView(this)
                objectText.text = "Detected Object: $detectedObject"
                objectText.textSize = 17f
                objectText.setTextColor(0xFF1B5E20.toInt())

                val materialText = TextView(this)
                materialText.text = "Material: $material"
                materialText.textSize = 15f
                materialText.setTextColor(0xFF2E7D32.toInt())
                materialText.setPadding(0, 8, 0, 0)

                val confidenceText = TextView(this)
                confidenceText.text = "Confidence: %.1f%%".format(confidence * 100)
                confidenceText.textSize = 14f
                confidenceText.setTextColor(0xFF666666.toInt())
                confidenceText.setPadding(0, 8, 0, 0)

                val dimensionText = TextView(this)
                dimensionText.textSize = 13f
                dimensionText.setTextColor(0xFF555555.toInt())
                dimensionText.setPadding(0, 12, 0, 0)

                val builder = StringBuilder()
                if (dimensions != null) {
                    val keys = dimensions.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        builder.append("$key: ${dimensions.getString(key)} cm\n")
                    }
                }

                dimensionText.text = builder.toString()

                textColumn.addView(objectText)
                textColumn.addView(materialText)
                textColumn.addView(confidenceText)
                textColumn.addView(dimensionText)

                container.addView(imageView)
                container.addView(textColumn)

                card.addView(container)
                rootLayout.addView(card)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(rootLayout, "Error parsing analysis result")
        }

        btnReuse.setOnClickListener {

            val intent = Intent(
                this,
                AIReuseSuggestionsActivity::class.java
            )

            // Pass full analysis JSON to next screen
            intent.putExtra("analysis_json", jsonString)

            startActivity(intent)
        }
    }

    private fun showMessage(layout: LinearLayout, message: String) {
        val textView = TextView(this)
        textView.textSize = 18f
        textView.setTextColor(0xFFD32F2F.toInt())
        textView.text = message
        layout.addView(textView)
    }
}