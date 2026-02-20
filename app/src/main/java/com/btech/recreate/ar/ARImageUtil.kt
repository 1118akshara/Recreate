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

        if (view.width == 0 || view.height == 0) {
            callback(null)
            return
        }

        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )

        PixelCopy.request(
            view,                     // source
            bitmap,                   // destination
            { copyResult ->           // callback
                if (copyResult == PixelCopy.SUCCESS) {
                    println("📷 Captured: ${bitmap.width} x ${bitmap.height}")
                    callback(bitmap)
                } else {
                    callback(null)
                }
            },
            Handler(Looper.getMainLooper()) // handler
        )
    }
}