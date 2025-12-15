package com.btech.recreate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // THIS LINE IS REQUIRED
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btn_scan).setOnClickListener {
            startActivity(Intent(this, ARScanActivity::class.java))
        }

        findViewById<Button>(R.id.btn_saved).setOnClickListener {
            startActivity(Intent(this, SavedProjectsActivity::class.java))
        }
    }
}
