package com.btech.recreate

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Config
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ux.ArFragment
import kotlin.math.pow
import kotlin.math.sqrt

class ARScanActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var txtHint: TextView
    private val tappedPoints = mutableListOf<Pose>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_scan)

        arFragment =
            supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        txtHint = findViewById(R.id.txtHint)
        txtHint.text = "Move phone slowly • Wait for surface • Tap 2 points"

        // ✅ Configure ARCore ONCE (CRITICAL FIX)
        arFragment.arSceneView.post {
            val session = arFragment.arSceneView.session ?: return@post
            val config = Config(session)

            // Plane detection
            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

            // Instant placement (helps thin objects)
            config.instantPlacementMode =
                Config.InstantPlacementMode.LOCAL_Y_UP

            // Depth mode (helps stability)
            if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                config.depthMode = Config.DepthMode.AUTOMATIC
            }

            session.configure(config)
        }

        // ✅ Plane tap handling
        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->

            if (plane.trackingState != TrackingState.TRACKING) {
                Toast.makeText(
                    this,
                    "Surface not stable yet. Move phone slowly.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnTapArPlaneListener
            }

            tappedPoints.add(hitResult.hitPose)

            if (tappedPoints.size == 2) {
                val distance = calculateDistance(
                    tappedPoints[0],
                    tappedPoints[1]
                )

                Toast.makeText(
                    this,
                    "Measured distance: %.2f meters".format(distance),
                    Toast.LENGTH_LONG
                ).show()

                tappedPoints.clear()
            }
        }
    }

    private fun calculateDistance(p1: Pose, p2: Pose): Float {
        val dx = p1.tx() - p2.tx()
        val dy = p1.ty() - p2.ty()
        val dz = p1.tz() - p2.tz()
        return sqrt(dx.pow(2) + dy.pow(2) + dz.pow(2))
    }
}
