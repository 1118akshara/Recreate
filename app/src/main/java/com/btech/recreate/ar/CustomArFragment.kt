package com.btech.recreate.ar

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session): Config {

        val config = super.getSessionConfiguration(session)

        config.planeFindingMode =
            Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.depthMode = Config.DepthMode.AUTOMATIC
        }

        // DO NOT call setupSession()
        // DO NOT override getSessionFeatures()

        return config
    }
}