package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import com.btech.recreate.data.ScanRepository
import com.btech.recreate.network.BackendApi

class ScanSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_list)

        val rootLayout = findViewById<LinearLayout>(R.id.rootLayout)
        val btnScanNext = findViewById<Button>(R.id.btnScanNext)
        val btnAnalyze = findViewById<Button>(R.id.btnAnalyze)


        displaySavedObjects(rootLayout)

        // 🔥 Scan next object
        btnScanNext.setOnClickListener {
            startActivity(Intent(this, ARScanActivity::class.java))
        }

        // 🔥 AI Analysis (FIXED)
        btnAnalyze.setOnClickListener {

            if (ScanRepository.scannedObjects.isEmpty()) {
                Toast.makeText(
                    this,
                    "No objects to analyze",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Uploading objects...",
                Toast.LENGTH_SHORT
            ).show()

            BackendApi.uploadSession(
                ScanRepository.scannedObjects
            ) { result ->

                runOnUiThread {

                    if (result == null) {
                        Toast.makeText(
                            this,
                            "Server connection failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@runOnUiThread
                    }

                    val intent = Intent(
                        this,
                        AnalysisResultActivity2::class.java
                    )

                    intent.putExtra("analysis_result", result)
                    startActivity(intent)
                }
            }
        }



    }

    private fun displaySavedObjects(rootLayout: LinearLayout) {

        val scannedObjects = ScanRepository.scannedObjects

        scannedObjects.forEachIndexed { index, obj ->

            // 🔥 Card container
            val card = LinearLayout(this)
            card.orientation = LinearLayout.HORIZONTAL
            card.setPadding(20, 20, 20, 20)
            card.setBackgroundColor(0xFFFFFFFF.toInt())

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            cardParams.setMargins(0, 0, 0, 20)
            card.layoutParams = cardParams

            // 🔥 Small Image
            val imageView = android.widget.ImageView(this)
            val imageParams = LinearLayout.LayoutParams(200, 200)
            imageParams.setMargins(0, 0, 20, 0)
            imageView.layoutParams = imageParams
            imageView.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP

            if (!obj.imagePath.isNullOrEmpty()) {
                val file = java.io.File(obj.imagePath!!)
                if (file.exists()) {
                    imageView.setImageURI(android.net.Uri.fromFile(file))
                }
            }

            // 🔥 Text container (Vertical)
            val textContainer = LinearLayout(this)
            textContainer.orientation = LinearLayout.VERTICAL
            textContainer.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            val builder = StringBuilder()

            builder.append("Object ${index + 1}\n")
            builder.append("Height: %.2f cm\n".format(obj.heightCm))

            when (obj.objectType) {
                "VERY_FLAT" -> {
                    builder.append("Width: %.2f cm\n".format(obj.widthCm))
                }
                "CYLINDRICAL" -> {
                    builder.append("Diameter: %.2f cm\n".format(obj.widthCm))
                }
                else -> {
                    builder.append("Width: %.2f cm\n".format(obj.widthCm))
                    builder.append("Depth: %.2f cm\n".format(obj.depthCm))
                }
            }

            val textView = TextView(this)
            textView.text = builder.toString()
            textView.textSize = 15f
            textView.setTextColor(0xFF1B5E20.toInt())

            textContainer.addView(textView)

            // Add image + text to card
            card.addView(imageView)
            card.addView(textContainer)

            rootLayout.addView(card)
        }
    }
}