package com.btech.recreate.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.ar.ARImageUtil
import com.btech.recreate.data.CaptureFrame
import com.btech.recreate.data.ObjectScanSession
import com.btech.recreate.R
import com.btech.recreate.data.ScanRepository
import com.btech.recreate.data.ViewType
import com.google.ar.sceneform.ux.ArFragment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ARScanActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var txtInstruction: TextView
    private lateinit var txtObjectInfo: TextView

    private val viewOrder = ViewType.values().toList()
    private var currentViewIndex = 0
    private var objectCount = 1

    // Retake mode variables
    private var isRetakeMode = false
    private var retakeObjectIndex = -1
    private var retakeViewType: ViewType? = null

    private var currentObject =
        ObjectScanSession(UUID.randomUUID().toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_scan)

        arFragment =
            supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        txtInstruction = findViewById(R.id.txt_instruction)
        txtObjectInfo = findViewById(R.id.txt_object_info)

        // 🔹 Check if this is retake mode
        isRetakeMode = intent.getBooleanExtra("retakeMode", false)
        retakeObjectIndex = intent.getIntExtra("objectIndex", -1)
        val viewName = intent.getStringExtra("viewType")

        if (isRetakeMode && viewName != null) {
            retakeViewType = ViewType.valueOf(viewName)
            objectCount = retakeObjectIndex + 1
            txtObjectInfo.text = "Retaking Object $objectCount"
            txtInstruction.text = "Retake ${retakeViewType!!.displayName}"
        } else {
            updateTexts()
        }

        findViewById<Button>(R.id.btn_capture_view).setOnClickListener {
            captureView()
        }

        findViewById<Button>(R.id.btn_next_object).setOnClickListener {
            startNextObject()
        }

        findViewById<Button>(R.id.btn_finish).setOnClickListener {
            startActivity(Intent(this, ScanSummaryActivity::class.java))
            finish()
        }
    }

    private fun captureView() {
        val frame = arFragment.arSceneView.arFrame ?: return
        val camera = frame.camera

        val poseMatrix = FloatArray(16)
        camera.pose.toMatrix(poseMatrix, 0)

        ARImageUtil.capture(this, arFragment.arSceneView) { bitmap ->
            if (bitmap == null) {
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
                return@capture
            }

            val imagePath = saveBitmap(bitmap)

            // 🔁 RETAKE MODE
            if (isRetakeMode && retakeViewType != null) {
                val obj = ScanRepository.scannedObjects[retakeObjectIndex]
                obj.captures[retakeViewType!!] =
                    CaptureFrame(
                        retakeViewType!!,
                        imagePath,
                        poseMatrix,
                        System.currentTimeMillis()
                    )

                Toast.makeText(
                    this,
                    "${retakeViewType!!.displayName} updated",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@capture
            }

            // 🔹 NORMAL SCAN MODE
            val viewType = viewOrder[currentViewIndex]

            currentObject.captures[viewType] =
                CaptureFrame(viewType, imagePath, poseMatrix, System.currentTimeMillis())

            currentViewIndex++

            if (currentViewIndex < viewOrder.size) {
                updateTexts()
            } else {
                ScanRepository.scannedObjects.add(currentObject)
                showCompletionButtons()
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap): String {
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "recreate_scans"
        )
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "IMG_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        return file.absolutePath
    }

    private fun updateTexts() {
        txtObjectInfo.text = "Object $objectCount"
        txtInstruction.text = "Capture ${viewOrder[currentViewIndex].displayName}"
    }

    private fun showCompletionButtons() {
        findViewById<Button>(R.id.btn_capture_view).isEnabled = false
        findViewById<Button>(R.id.btn_next_object).visibility = Button.VISIBLE
        findViewById<Button>(R.id.btn_finish).visibility = Button.VISIBLE
    }

    private fun startNextObject() {
        objectCount++
        currentViewIndex = 0
        currentObject = ObjectScanSession(UUID.randomUUID().toString())

        findViewById<Button>(R.id.btn_capture_view).isEnabled = true
        findViewById<Button>(R.id.btn_next_object).visibility = Button.GONE
        findViewById<Button>(R.id.btn_finish).visibility = Button.GONE

        updateTexts()
    }
}