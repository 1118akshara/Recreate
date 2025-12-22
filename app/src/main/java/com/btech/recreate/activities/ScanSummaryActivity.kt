package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.btech.recreate.data.ScanRepository

class ScanSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        ScanRepository.scannedObjects.forEachIndexed { index, obj ->
            val card = CardView(this).apply {
                radius = 16f
                setContentPadding(24, 24, 24, 24)
                useCompatPadding = true
            }

            val content = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            val title = TextView(this).apply {
                text = "Object ${index + 1}"
                textSize = 18f
            }
            content.addView(title)

            obj.captures.values.forEach { frame ->
                val viewText = TextView(this).apply {
                    text = "• ${frame.viewType.displayName}"
                    textSize = 16f
                    setPadding(0, 12, 0, 12)
                    setOnClickListener {
                        val intent =
                            Intent(this@ScanSummaryActivity, ImagePreviewActivity::class.java)
                        intent.putExtra("objectIndex", index)
                        intent.putExtra("viewType", frame.viewType.name)
                        startActivity(intent)
                    }
                }
                content.addView(viewText)
            }

            card.addView(content)
            root.addView(card)
        }

        // 🔹 Analyze Button
        val analyzeButton = Button(this).apply {
            text = "Analyze Objects (AI)"
            textSize = 16f
            setOnClickListener {
                startActivity(Intent(this@ScanSummaryActivity, AnalysisResultActivity::class.java))
            }
        }

        root.addView(analyzeButton)

        setContentView(root)
    }
}