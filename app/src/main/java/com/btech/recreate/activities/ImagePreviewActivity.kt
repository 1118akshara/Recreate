package com.btech.recreate.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.data.ScanRepository
import com.btech.recreate.data.ViewType
import java.io.File

class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val objectIndex = intent.getIntExtra("objectIndex", -1)
        val viewTypeName = intent.getStringExtra("viewType")

        if (objectIndex < 0 || viewTypeName == null) {
            Toast.makeText(this, "Invalid preview data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val viewType = ViewType.valueOf(viewTypeName)
        val frame =
            ScanRepository.scannedObjects[objectIndex].captures[viewType]!!

        val imageFile = File(frame.imagePath)

        val title = TextView(this).apply {
            text = "Object ${objectIndex + 1} • ${viewType.displayName}"
            textSize = 20f
        }

        val imageView = ImageView(this).apply {
            setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))
            adjustViewBounds = true
        }

        val retakeButton = Button(this).apply {
            text = "Retake ${viewType.displayName}"
            setOnClickListener {
                val intent = Intent(this@ImagePreviewActivity, ARScanActivity::class.java)
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

        val scroll = ScrollView(this).apply {
            addView(content)
        }

        setContentView(scroll)
    }
}