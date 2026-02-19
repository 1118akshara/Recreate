package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import com.btech.recreate.data.ScanRepository

class ScanSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_list)

        val rootLayout = findViewById<LinearLayout>(R.id.rootLayout)
        val btnScanNext = findViewById<Button>(R.id.btnScanNext)
        val btnAnalyze = findViewById<Button>(R.id.btnAnalyze)
        val btnReuse = findViewById<Button>(R.id.btnReuse)
        val btnFinish = findViewById<Button>(R.id.btnFinish)

        displaySavedObjects(rootLayout)

        // 🔥 Scan next object
        btnScanNext.setOnClickListener {
            startActivity(Intent(this, ARScanActivity::class.java))
        }

        // 🔥 AI Analysis
        btnAnalyze.setOnClickListener {
            startActivity(Intent(this, AnalysisResultActivity2::class.java))
        }

        // 🔥 Reuse Suggestions
        btnReuse.setOnClickListener {
            startActivity(Intent(this, AIReuseSuggestionsActivity::class.java))
        }

        // 🔥 Finish Scan → Go Home
        btnFinish.setOnClickListener {
            ScanRepository.scannedObjects.clear()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun displaySavedObjects(rootLayout: LinearLayout) {

        val scannedObjects = ScanRepository.scannedObjects

        scannedObjects.forEachIndexed { index, obj ->

            val textView = TextView(this)
            textView.textSize = 16f
            textView.setPadding(0, 16, 0, 16)

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

            textView.text = builder.toString()
            rootLayout.addView(textView, rootLayout.childCount - 4)
        }
    }
}
