package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.btech.recreate.data.ScanRepository

class ScanSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        scrollView.addView(rootLayout)

        val title = TextView(this).apply {
            text = "Scan Summary"
            textSize = 22f
            setPadding(0, 0, 0, 24)
        }

        rootLayout.addView(title)

        // 🔹 Show All Objects
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

            val dimensionText = TextView(this).apply {
                text = """
                    Height: ${"%.2f".format(obj.heightCm ?: 0f)} cm
                    Width: ${"%.2f".format(obj.widthCm ?: 0f)} cm
                    Depth: ${"%.2f".format(obj.depthCm ?: 0f)} cm
                """.trimIndent()
                textSize = 14f
                setPadding(0, 8, 0, 16)
            }

            content.addView(header)
            content.addView(dimensionText)

            card.addView(content)
            rootLayout.addView(card)

            val spacer = TextView(this)
            spacer.height = 24
            rootLayout.addView(spacer)
        }

        // 🔹 Scan Next Object
        val scanNextButton = Button(this).apply {
            text = "Scan Next Object"
            textSize = 16f
            setOnClickListener {
                startActivity(
                    Intent(
                        this@ScanSummaryActivity,
                        ARScanActivity::class.java
                    )
                )
            }
        }

        rootLayout.addView(scanNextButton)

        // 🔹 Finish Session
        val finishButton = Button(this).apply {
            text = "Finish Session"
            textSize = 16f
            setOnClickListener {
                ScanRepository.scannedObjects.clear()
                finish()
            }
        }

        rootLayout.addView(finishButton)

        setContentView(scrollView)
    }
}
