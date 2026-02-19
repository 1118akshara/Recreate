package com.btech.recreate.ar

import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.LinearInterpolator
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment

class ARRulerManager(
    private val context: Context,
    private val arFragment: ArFragment,
    private val listener: MeasurementListener
) {

    interface MeasurementListener {
        fun onMeasured(distanceCm: Float)
    }

    private var firstAnchor: AnchorNode? = null
    private var secondAnchor: AnchorNode? = null
    private var lineNode: Node? = null
    private var glowAnimator: ValueAnimator? = null

    private var markerSize = 0.008f

    fun setMarkerSize(size: Float) {
        markerSize = size
    }

    fun addPoint(hitResult: HitResult) {

        val trackable = hitResult.trackable
        if (trackable !is Plane) return

        val anchor = hitResult.createAnchor()
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment.arSceneView.scene)

        MaterialFactory.makeOpaqueWithColor(
            context,
            Color(android.graphics.Color.RED)
        ).thenAccept { material ->

            val sphere = ShapeFactory.makeSphere(
                markerSize,
                Vector3.zero(),
                material
            )
            anchorNode.renderable = sphere
        }

        if (firstAnchor == null) {
            firstAnchor = anchorNode
        } else if (secondAnchor == null) {
            secondAnchor = anchorNode
            drawLine()
        }
    }

    private fun drawLine() {

        val start = firstAnchor?.worldPosition ?: return
        val end = secondAnchor?.worldPosition ?: return

        val difference = Vector3.subtract(end, start)
        val distanceMeters = difference.length().coerceAtLeast(0.001f)
        val distanceCm = distanceMeters * 100f

        val midpoint = Vector3.add(start, end).scaled(0.5f)

        MaterialFactory.makeOpaqueWithColor(
            context,
            Color(android.graphics.Color.YELLOW)
        ).thenAccept { material ->

            val cylinder = ShapeFactory.makeCylinder(
                0.0035f,
                distanceMeters,
                Vector3.zero(),
                material
            )

            lineNode = Node().apply {
                setParent(arFragment.arSceneView.scene)
                renderable = cylinder
                worldPosition = midpoint
            }

            val direction = difference.normalized()
            val rotation = Quaternion.rotationBetweenVectors(
                Vector3(0f, 1f, 0f),
                direction
            )

            lineNode?.worldRotation = rotation

            startGlow(material)
        }

        listener.onMeasured(distanceCm)
    }

    private fun startGlow(material: Material) {

        glowAnimator = ValueAnimator.ofFloat(0.6f, 1f).apply {
            duration = 800
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()

            addUpdateListener {
                val value = it.animatedValue as Float
                material.setFloat3("baseColorTint", value, value, 0f)
            }

            start()
        }
    }

    fun clearMeasurement() {

        glowAnimator?.cancel()
        glowAnimator = null

        firstAnchor?.setParent(null)
        secondAnchor?.setParent(null)
        lineNode?.setParent(null)

        firstAnchor = null
        secondAnchor = null
        lineNode = null
    }
}
