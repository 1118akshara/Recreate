package com.btech.recreate.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import com.btech.recreate.ar.ARImageUtil
import com.btech.recreate.ar.ARRulerManager
import com.btech.recreate.data.ObjectScanSession
import com.btech.recreate.data.ScanRepository
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.sceneform.ux.ArFragment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ARScanActivity : AppCompatActivity(),
    ARRulerManager.MeasurementListener {

    private lateinit var arFragment: ArFragment
    private lateinit var txtInstruction: TextView
    private lateinit var txtObjectInfo: TextView
    private lateinit var txtLiveMeasurement: TextView
    private lateinit var btnCapture: Button
    private lateinit var rulerManager: ARRulerManager

    private var measurementStep = 0
    private var objectCount = 1

    private var currentObject =
        ObjectScanSession(UUID.randomUUID().toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_scan)

        objectCount = ScanRepository.scannedObjects.size + 1

        arFragment =
            supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        txtInstruction = findViewById(R.id.txt_instruction)
        txtObjectInfo = findViewById(R.id.txt_object_info)
        txtLiveMeasurement = findViewById(R.id.txt_live_measurement)
        btnCapture = findViewById(R.id.btn_capture_view)

        rulerManager = ARRulerManager(this, arFragment, this)

        arFragment.arSceneView.scene.addOnUpdateListener {
            val session = arFragment.arSceneView.session ?: return@addOnUpdateListener
            val config = session.config
            config.focusMode = Config.FocusMode.AUTO
            config.planeFindingMode =
                Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
            session.configure(config)
        }

        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            if (measurementStep < 3 &&
                (plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING ||
                        plane.type == Plane.Type.VERTICAL)
            ) {
                rulerManager.addPoint(hitResult)
            }
        }

        btnCapture.setOnClickListener {
            if (measurementStep < 3) {
                Toast.makeText(
                    this,
                    "Complete all measurements first",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                captureImage()
            }
        }

        updateUI()
    }

    override fun onMeasured(distanceCm: Float) {

        txtLiveMeasurement.text = "%.2f cm".format(distanceCm)

        when (measurementStep) {
            0 -> currentObject.heightCm = distanceCm
            1 -> currentObject.widthCm = distanceCm
            2 -> currentObject.depthCm = distanceCm
        }

        measurementStep++
        updateUI()
    }

    private fun updateUI() {

        txtObjectInfo.text = "Object $objectCount"

        when (measurementStep) {
            0 -> txtInstruction.text = "Measure Height (tap two points)"
            1 -> txtInstruction.text = "Measure Width (tap two points)"
            2 -> txtInstruction.text = "Measure Depth (tap two points)"
            else -> {
                txtInstruction.text = "All dimensions captured\nTap Capture Image"
                btnCapture.isEnabled = true
            }
        }
    }

    private fun captureImage() {

        ARImageUtil.capture(this, arFragment.arSceneView) { bitmap ->
            if (bitmap == null) {
                Toast.makeText(this, "Capture failed", Toast.LENGTH_SHORT).show()
                return@capture
            }

            val imagePath = saveBitmap(bitmap)
            currentObject.imagePath = imagePath

            ScanRepository.scannedObjects.add(currentObject)

            Toast.makeText(this, "Object $objectCount saved!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, ScanSummaryActivity::class.java))
            finish()
        }
    }

    private fun saveBitmap(bitmap: Bitmap): String {

        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "recreate_scans"
        )
        if (!dir.exists()) dir.mkdirs()

        val fileName = "obj${objectCount}.jpg"
        val file = File(dir, fileName)

        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }

        return file.absolutePath
    }
}
