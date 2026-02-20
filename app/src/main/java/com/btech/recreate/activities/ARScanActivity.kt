package com.btech.recreate.activities

import android.app.AlertDialog
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

import com.google.ar.core.Plane

import com.btech.recreate.ar.CustomArFragment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ARScanActivity : AppCompatActivity(),
    ARRulerManager.MeasurementListener {

    private lateinit var arFragment: CustomArFragment
    private lateinit var txtInstruction: TextView
    private lateinit var txtObjectInfo: TextView
    private lateinit var txtLiveMeasurement: TextView
    private lateinit var btnCapture: Button
    private lateinit var rulerManager: ARRulerManager

    private var measurementStep = 0
    private var objectCount = 1
    private var objectType: ObjectType = ObjectType.CUBOIDAL

    private var currentObject =
        ObjectScanSession(UUID.randomUUID().toString())

    enum class ObjectType {
        FLAT,
        VERY_FLAT,
        CUBOIDAL,
        CYLINDRICAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_scan)

        objectCount = ScanRepository.scannedObjects.size + 1

        arFragment =
            supportFragmentManager.findFragmentById(R.id.arFragment) as CustomArFragment

        txtInstruction = findViewById(R.id.txt_instruction)
        txtObjectInfo = findViewById(R.id.txt_object_info)
        txtLiveMeasurement = findViewById(R.id.txt_live_measurement)
        btnCapture = findViewById(R.id.btn_capture_view)

        rulerManager = ARRulerManager(this, arFragment, this)

        showObjectTypeDialog()

        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            if (measurementStep < getMaxSteps() &&
                (plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING ||
                        plane.type == Plane.Type.VERTICAL)
            ) {
                rulerManager.addPoint(hitResult)
            }
        }

        btnCapture.setOnClickListener {
            if (measurementStep < getMaxSteps()) {
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



    private fun getMaxSteps(): Int {
        return when (objectType) {
            ObjectType.CYLINDRICAL -> 2
            ObjectType.VERY_FLAT -> 2
            else -> 3
        }
    }

    private fun showObjectTypeDialog() {

        val options = arrayOf(
            "Flat Object",
            "Very Flat Object",
            "Cuboidal Object",
            "Cylindrical Object"
        )

        AlertDialog.Builder(this)
            .setTitle("Select Object Type")
            .setCancelable(false)
            .setItems(options) { _, which ->

                objectType = when (which) {
                    0 -> ObjectType.FLAT
                    1 -> ObjectType.VERY_FLAT
                    2 -> ObjectType.CUBOIDAL
                    else -> ObjectType.CYLINDRICAL
                }

                when (objectType) {
                    ObjectType.FLAT -> rulerManager.setMarkerSize(0.005f)
                    else -> rulerManager.setMarkerSize(0.008f)
                }

                currentObject.objectType = objectType.name
                measurementStep = 0
                rulerManager.clearMeasurement()
                updateUI()
            }
            .show()
    }

    override fun onMeasured(distanceCm: Float) {

        txtLiveMeasurement.text = "%.2f cm".format(distanceCm)

        when (objectType) {

            ObjectType.FLAT -> {
                if (measurementStep == 0) currentObject.heightCm = distanceCm
                if (measurementStep == 1) currentObject.widthCm = distanceCm
                if (measurementStep == 2) currentObject.depthCm = distanceCm
            }

            ObjectType.VERY_FLAT -> {
                if (measurementStep == 0) currentObject.heightCm = distanceCm
                if (measurementStep == 1) currentObject.widthCm = distanceCm
            }

            ObjectType.CUBOIDAL -> {
                if (measurementStep == 0) currentObject.heightCm = distanceCm
                if (measurementStep == 1) currentObject.widthCm = distanceCm
                if (measurementStep == 2) currentObject.depthCm = distanceCm
            }

            ObjectType.CYLINDRICAL -> {
                if (measurementStep == 0) currentObject.heightCm = distanceCm
                if (measurementStep == 1) currentObject.widthCm = distanceCm
            }
        }

        measurementStep++

        txtLiveMeasurement.postDelayed({
            rulerManager.clearMeasurement()
            updateUI()
        }, 800)
    }

    private fun updateUI() {

        txtObjectInfo.text = "Object $objectCount"

        when (objectType) {

            ObjectType.FLAT -> {
                when (measurementStep) {
                    0 -> txtInstruction.text = "Measure Height"
                    1 -> txtInstruction.text = "Measure Width"
                    2 -> txtInstruction.text = "Measure Depth"
                    else -> enableCapture()
                }
            }

            ObjectType.VERY_FLAT -> {
                when (measurementStep) {
                    0 -> txtInstruction.text = "Measure Height"
                    1 -> txtInstruction.text = "Measure Width"
                    else -> enableCapture()
                }
            }

            ObjectType.CUBOIDAL -> {
                when (measurementStep) {
                    0 -> txtInstruction.text = "Measure Height"
                    1 -> txtInstruction.text = "Measure Width"
                    2 -> txtInstruction.text = "Measure Depth"
                    else -> enableCapture()
                }
            }

            ObjectType.CYLINDRICAL -> {
                when (measurementStep) {
                    0 -> txtInstruction.text = "Measure Height"
                    1 -> txtInstruction.text = "Measure Diameter"
                    else -> enableCapture()
                }
            }
        }
    }

    private fun enableCapture() {
        txtInstruction.text = "All dimensions captured. Tap Capture."
        btnCapture.isEnabled = true
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

            Toast.makeText(this, "Object saved!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, ScanSummaryActivity::class.java))

            currentObject = ObjectScanSession(UUID.randomUUID().toString())
            measurementStep = 0
        }
    }

    private fun saveBitmap(bitmap: Bitmap): String {

        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "recreate_scans"
        )

        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "obj${objectCount}.jpg")

        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        return file.absolutePath
    }
}