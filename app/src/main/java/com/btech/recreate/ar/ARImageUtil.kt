package com.btech.recreate.ar

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import com.google.ar.sceneform.ArSceneView

object ARImageUtil {

    fun capture(
        activity: Activity,
        view: ArSceneView,
        callback: (Bitmap?) -> Unit
    ) {
        val bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        PixelCopy.request(
            view,
            bitmap,
            { result ->
                if (result == PixelCopy.SUCCESS) {
                    callback(bitmap)
                } else {
                    callback(null)
                }
            },
            Handler(Looper.getMainLooper())
        )
    }
}