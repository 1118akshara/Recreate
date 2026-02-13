package com.btech.recreate.ar

import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.LinearInterpolator
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
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

    private var firstAnchorNode: AnchorNode? = null
    private var secondAnchorNode: AnchorNode? = null
    private var lineNode: Node? = null
    private var glowAnimator: ValueAnimator? = null

    fun addPoint(hitResult: HitResult) {

        // If already measured, clear previous visuals
        if (firstAnchorNode != null && secondAnchorNode != null) {
            clearLine()
        }

        val anchor = hitResult.createAnchor()
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment.arSceneView.scene)

        // 🔴 Create Red Sphere
        MaterialFactory.makeOpaqueWithColor(
            context,
            Color(android.graphics.Color.RED)
        ).thenAccept { material ->

            val sphere = ShapeFactory.makeSphere(
                0.015f,
                Vector3.zero(),
                material
            )

            anchorNode.renderable = sphere
        }

        if (firstAnchorNode == null) {
            firstAnchorNode = anchorNode
        } else if (secondAnchorNode == null) {
            secondAnchorNode = anchorNode
            drawAnimatedLine()
        }
    }

    private fun drawAnimatedLine() {

        val start = firstAnchorNode!!.worldPosition
        val end = secondAnchorNode!!.worldPosition

        val difference = Vector3.subtract(end, start)
        val distanceMeters = difference.length()
        val distanceCm = distanceMeters * 100f

        val midpoint = Vector3.add(start, end).scaled(0.5f)

        MaterialFactory.makeOpaqueWithColor(
            context,
            Color(android.graphics.Color.YELLOW)
        ).thenAccept { material ->

            val cylinder = ShapeFactory.makeCylinder(
                0.004f,              // thinner line
                distanceMeters,
                Vector3.zero(),
                material
            )

            lineNode = Node()
            lineNode!!.setParent(arFragment.arSceneView.scene)
            lineNode!!.renderable = cylinder
            lineNode!!.worldPosition = midpoint

            val direction = difference.normalized()
            val rotation = Quaternion.rotationBetweenVectors(
                Vector3(0f, 1f, 0f),
                direction
            )

            lineNode!!.worldRotation = rotation

            startGlowAnimation(material)
        }

        listener.onMeasured(distanceCm)
    }

    // ✨ Twinkling Glow Effect
    private fun startGlowAnimation(material: com.google.ar.sceneform.rendering.Material) {

        glowAnimator = ValueAnimator.ofFloat(0.4f, 1.0f)
        glowAnimator?.duration = 1200
        glowAnimator?.repeatMode = ValueAnimator.REVERSE
        glowAnimator?.repeatCount = ValueAnimator.INFINITE
        glowAnimator?.interpolator = LinearInterpolator()

        glowAnimator?.addUpdateListener { animator ->
            val intensity = animator.animatedValue as Float
            material.setFloat3(
                "baseColorTint",
                intensity,
                intensity,
                0f     // yellow glow
            )
        }

        glowAnimator?.start()
    }

    fun clearLine() {

        glowAnimator?.cancel()
        glowAnimator = null

        lineNode?.setParent(null)
        firstAnchorNode?.setParent(null)
        secondAnchorNode?.setParent(null)

        lineNode = null
        firstAnchorNode = null
        secondAnchorNode = null
    }
}
