package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.btech.recreate.R
import com.btech.recreate.ar.ARTip
import com.btech.recreate.ar.ARTipsAdapter
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔥 Start Scan Button
        findViewById<Button>(R.id.btn_scan).setOnClickListener {
            startActivity(Intent(this, ARScanActivity::class.java))
        }

        // 🔥 Saved Projects Button
        findViewById<Button>(R.id.btn_saved_projects).setOnClickListener {
            startActivity(Intent(this, SavedProjectsActivity::class.java))
        }

        // 🔥 AR Smart Guidelines Carousel
        val arTipsPager = findViewById<ViewPager2>(R.id.arTipsPager)

        val tipsList = listOf(
            ARTip(
                "💡",
                "Ensure Good Lighting",
                "Ensure proper lighting for better object detection."
            ),
            ARTip(
                "📐",
                "Detect Flat Surfaces",
                "Scan tables or floors first for stable placement."
            ),
            ARTip(
                "🧭",
                "Scan Multiple Angles",
                "Capture object from multiple angles for accurate dimensions."
            ),
            ARTip(
                "📖",
                "View Full User Guidelines",
                "Tap here to explore detailed AR usage instructions.",
                true
            )
        )

        arTipsPager.adapter = ARTipsAdapter(tipsList)

        // ✅ Premium Peek Effect
        arTipsPager.clipToPadding = false
        arTipsPager.clipChildren = false
        arTipsPager.offscreenPageLimit = 3
        arTipsPager.setPadding(60, 0, 60, 0)

        // ✅ Smooth Scale Animation
        arTipsPager.setPageTransformer { page, position ->
            val scale = 0.9f + (1 - abs(position)) * 0.1f
            page.scaleY = scale
        }
    }
}