package com.edutrack.core.di

import co.touchlab.kermit.Logger
import com.edutrack.core.data.auth.AuthRepository
import com.edutrack.core.presentation.auth.AuthViewModel
// [firebase_auth] start
// import com.edutrack.core.data.auth.FirebaseAuthHandler
// import com.edutrack.core.data.auth.SocialAuthBackendHandler
// [firebase_auth] end
// [firestore] start
// import com.edutrack.core.data.firestore.UserProfileSync
// [firestore] end
import com.edutrack.core.data.gsheets.GoogleSheetsConfig
import com.edutrack.core.data.gsheets.GoogleSheetsService
// Note: AiChatService import is in AppModule.kt (project-specific implementation)
// [payment] start
// import com.edutrack.core.data.payment.PaymentConfigService
// import com.edutrack.core.data.payment.PaymentService
// import com.edutrack.core.data.payment.RevenueCatPaymentService
// import com.edutrack.core.domain.usecase.GetPaywallConfigUseCase
// import com.edutrack.core.domain.usecase.CheckFeatureAccessUseCase
// [payment] end
// Note: RealtimeDbService is registered in platform modules (Android/iOS)
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect fun platformModule() : Module

fun coreModule() = module {

    factory {
        HttpClient(get()) {
            install(HttpTimeout) {
                socketTimeoutMillis = 120_000  // 2 minutes
                requestTimeoutMillis = 120_000 // 2 minutes
                connectTimeoutMillis = 60_000  // 60 seconds
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 1)
                exponentialDelay()
                modifyRequest { request ->
                    request.headers.append("x-retry-count", retryCount.toString())
                }
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Logger.d("ktor client external") {
                            message
                        }
                    }
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // Don't automatically throw exceptions for HTTP error status codes
            // This allows our BaseService.handleResponse() to handle errors properly
            expectSuccess = false
        }
    }

    // [firestore] start
    // single { UserProfileSync(firestoreService = get()) }
    // [firestore] end

    single { AuthRepository(database = get(), backendHandler = getOrNull(), userProfileSync = getOrNull()) }
    viewModelOf(::AuthViewModel)

    // [firebase_auth] start
    // single<SocialAuthBackendHandler> { FirebaseAuthHandler() }
    // [firebase_auth] end

    single { GoogleSheetsConfig(get()) }
    single { GoogleSheetsService(get()) }

    // Note: AiChatService implementation is registered in AppModule.kt (project-specific)

    // [payment] start
    // single { PaymentConfigService(get(), get()) }
    // single<PaymentService> { RevenueCatPaymentService(get()) }
    // factory { GetPaywallConfigUseCase(get()) }
    // factory { CheckFeatureAccessUseCase(get(), get()) }
    // [payment] end

}
