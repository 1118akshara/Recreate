package com.btech.recreate.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.data.ScanRepository
import com.btech.recreate.data.ViewType
import java.io.File

class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Read intent data safely
        val objectIndex = intent.getIntExtra("objectIndex", -1)
        val viewTypeName = intent.getStringExtra("viewType")

        if (objectIndex < 0 || viewTypeName == null) {
            Toast.makeText(this, "Invalid preview request", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val viewType = try {
            ViewType.valueOf(viewTypeName)
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid view type", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val objectData = ScanRepository.scannedObjects.getOrNull(objectIndex)
        if (objectData == null) {
            Toast.makeText(this, "Object not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val frame = objectData.captures[viewType]
        if (frame == null) {
            Toast.makeText(this, "Image not available for this view", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val imageFile = File(frame.imagePath)
        if (!imageFile.exists()) {
            Toast.makeText(this, "Image file missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🔹 UI
        val title = TextView(this).apply {
            text = "Object ${objectIndex + 1} • ${viewType.displayName}"
            textSize = 20f
            setPadding(0, 0, 0, 16)
        }

        val imageView = ImageView(this).apply {
            adjustViewBounds = true
            setImageURI(Uri.fromFile(imageFile))
        }

        val retakeButton = Button(this).apply {
            text = "Retake ${viewType.displayName}"
            setOnClickListener {

                // 🔥 Remove old image before retake
                objectData.captures.remove(viewType)

                val intent = Intent(
                    this@ImagePreviewActivity,
                    ARScanActivity::class.java
                )
                intent.putExtra("retakeMode", true)
                intent.putExtra("objectIndex", objectIndex)
                intent.putExtra("viewType", viewType.name)
                startActivity(intent)
                finish()
            }
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            addView(title)
            addView(imageView)
            addView(retakeButton)
        }

        val scrollView = ScrollView(this).apply {
            addView(content)
        }

        setContentView(scrollView)
    }
}
