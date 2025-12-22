package com.btech.recreate.ar

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session): Config {
        val config = Config(session)

        // Detect horizontal & vertical surfaces (white dots)
        config.planeFindingMode =
            Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        // Allows placing points even if plane is not fully detected
        config.instantPlacementMode =
            Config.InstantPlacementMode.LOCAL_Y_UP

        // Enable depth if device supports it
        config.depthMode =
            if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
                Config.DepthMode.AUTOMATIC
            else
                Config.DepthMode.DISABLED

        this.arSceneView.setupSession(session)
        return config
    }
}