package com.edutrack.core.di

import androidx.room.Room
// [deep_linking] start
// import com.edutrack.core.data.deeplink.DeepLinkHandler
// import com.edutrack.core.data.deeplink.DefaultDeepLinkHandler
// [deep_linking] end
import com.edutrack.core.data.local.AppDatabase
import com.edutrack.core.data.local.AppSettings
// [push_notifications] start
// import com.edutrack.core.data.notifications.IosPushNotificationService
// import com.edutrack.core.data.notifications.PushNotificationService
// [push_notifications] end
// [firestore] start
// import com.edutrack.core.data.firestore.FirestoreService
// import com.edutrack.core.data.firestore.getFirestoreService
// [firestore] end
// [messaging] start
// import com.edutrack.core.data.messaging.RealtimeDbService
// import com.edutrack.core.data.messaging.getRealtimeDbService
// [messaging] end
import com.edutrack.core.settings.IosAppSettings
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun platformModule() = module {
    single {
        // Get iOS Documents directory (proper location for databases)
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val dbPath = requireNotNull(documentDirectory?.path) + "/study_plan.db"

        Room.databaseBuilder<AppDatabase>(name = dbPath)
            .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())           // REQUIRED for KMP
            .setQueryCoroutineContext(Dispatchers.IO)   // REQUIRED for async
            .fallbackToDestructiveMigration(true)       // Dev convenience
            .build()
    }

    single {
        Darwin.create()
    }

    single<AppSettings> { IosAppSettings(NSUserDefaults.standardUserDefaults) }

    // [push_notifications] start
    // single<PushNotificationService> { IosPushNotificationService() }
    // [push_notifications] end

    // [deep_linking] start
    // single<DeepLinkHandler> { DefaultDeepLinkHandler() }
    // [deep_linking] end

    // [firestore] start
    // single<FirestoreService> { getFirestoreService() }
    // [firestore] end

    // [messaging] start
    // single<RealtimeDbService> { getRealtimeDbService() }
    // [messaging] end
}
