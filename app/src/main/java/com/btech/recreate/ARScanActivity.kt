package com.btech.recreate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk

class ARScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val availability = ArCoreApk.getInstance().checkAvailability(this)

        if (availability.isSupported) {
            Toast.makeText(this, "ARCore Ready", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "ARCore NOT supported on this device", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
