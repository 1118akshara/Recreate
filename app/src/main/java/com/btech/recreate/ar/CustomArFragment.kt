package com.btech.recreate.ar

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session): Config {

        val config = super.getSessionConfiguration(session)

        config.planeFindingMode =
            Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        config.instantPlacementMode =
            Config.InstantPlacementMode.LOCAL_Y_UP

        config.depthMode =
            if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
                Config.DepthMode.AUTOMATIC
            else
                Config.DepthMode.DISABLED

        this.arSceneView.setupSession(session)

        // 🔥 VERY IMPORTANT FOR WHITE DOTS
        planeDiscoveryController.show()
        planeDiscoveryController.setInstructionView(null)

        return config
    }
}
