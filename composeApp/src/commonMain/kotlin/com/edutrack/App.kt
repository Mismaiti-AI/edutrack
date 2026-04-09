package com.edutrack

import androidx.compose.runtime.*
import com.edutrack.di.moduleList
import com.edutrack.presentation.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration


@Composable
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(application = {
        modules(moduleList())
        koinAppDeclaration?.invoke(this)
    }) {
        AppTheme {
            AppContent()
        }
    }
}
