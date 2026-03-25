package com.edutrack

import android.app.Application
import co.touchlab.kermit.Logger
// [firebase] start
// import com.google.firebase.FirebaseApp
// [firebase] end

class EduTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // [firebase] start
        // FirebaseApp.initializeApp(this)
        // [firebase] end

        Logger.withTag("EduTrackApp").d("onCreate")
    }
}