package com.btech.recreate.ar

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session): Config {

        val config = super.getSessionConfiguration(session)

        // Keep only basic plane detection
        config.planeFindingMode =
            Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        // Keep autofocus only
        config.focusMode = Config.FocusMode.AUTO

        // ❌ REMOVE HDR
        // ❌ REMOVE Depth
        // ❌ REMOVE any other config

        return config
    }
}