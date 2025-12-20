package com.btech.recreate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Config
import com.google.ar.core.Pose
import com.google.ar.sceneform.ux.ArFragment
import kotlin.math.pow
import kotlin.math.sqrt

class ARScanActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private val points = mutableListOf<Pose>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_scan)

        arFragment =
            supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        // ✅ Enable Instant Placement (IMPORTANT)
        arFragment.arSceneView.scene.addOnUpdateListener {
            val session = arFragment.arSceneView.session ?: return@addOnUpdateListener
            val config = session.config
            config.instantPlacementMode =
                Config.InstantPlacementMode.LOCAL_Y_UP
            session.configure(config)
        }

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            points.add(hitResult.hitPose)

            if (points.size == 2) {
                val d = distance(points[0], points[1])
                Toast.makeText(
                    this,
                    "Distance: %.2f m".format(d),
                    Toast.LENGTH_LONG
                ).show()
                points.clear()
            }
        }
    }

    private fun distance(p1: Pose, p2: Pose): Float {
        val dx = p1.tx() - p2.tx()
        val dy = p1.ty() - p2.ty()
        val dz = p1.tz() - p2.tz()
        return sqrt(dx.pow(2) + dy.pow(2) + dz.pow(2))
    }
}
