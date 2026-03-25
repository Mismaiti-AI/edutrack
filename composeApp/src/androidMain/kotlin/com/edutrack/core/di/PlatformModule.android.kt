package com.edutrack.core.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
// [deep_linking] start
// import com.edutrack.core.data.deeplink.DeepLinkHandler
// import com.edutrack.core.data.deeplink.DefaultDeepLinkHandler
// [deep_linking] end
import com.edutrack.core.data.local.AppDatabase
import com.edutrack.core.data.local.AppSettings
// [push_notifications] start
// import com.edutrack.core.data.notifications.AndroidPushNotificationService
// import com.edutrack.core.data.notifications.PushNotificationService
// [push_notifications] end
// [firestore] start
// import com.edutrack.core.data.firestore.FirestoreService
// import com.edutrack.core.data.firestore.AndroidFirestoreService
// [firestore] end
// [messaging] start
// import com.edutrack.core.data.messaging.RealtimeDbService
// import com.edutrack.core.data.messaging.AndroidRealtimeDbService
// [messaging] end
import com.edutrack.core.settings.AndroidAppSettings
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule() = module {
    // Database
    single {
        val appContext = androidContext().applicationContext
        val dbFile = appContext.getDatabasePath("study_plan.db")
        Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single {
        OkHttp.create {
            preconfigured = getOkHttpClient()
        }
    }

    single<AppSettings> { AndroidAppSettings(androidContext()) }

    // [push_notifications] start
    // single<PushNotificationService> {
    //     AndroidPushNotificationService(androidContext()).also {
    //         AndroidPushNotificationService.instance = it
    //     }
    // }
    // [push_notifications] end

    // [deep_linking] start
    // single<DeepLinkHandler> { DefaultDeepLinkHandler() }
    // [deep_linking] end

    // [firestore] start
    // single<FirestoreService> { AndroidFirestoreService() }
    // [firestore] end

    // [messaging] start
    // single<RealtimeDbService> { AndroidRealtimeDbService() }
    // [messaging] end
}

private fun getOkHttpClient() = OkHttpClient.Builder().build()
