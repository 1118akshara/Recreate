package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // THIS LINE IS REQUIRED
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btn_scan).setOnClickListener {
            startActivity(Intent(this, ARScanActivity::class.java))
        }
        val btnSavedProjects = findViewById<Button>(R.id.btn_saved_projects)

        btnSavedProjects.setOnClickListener {
            val intent = Intent(this, SavedProjectsActivity::class.java)
            startActivity(intent)
        }



    }
}