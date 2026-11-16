package com.bookspine.readinglog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bookspine.readinglog.core.di.AppViewModelFactory
import com.bookspine.readinglog.core.di.ServiceLocator
import com.bookspine.readinglog.core.theme.AppTheme
import com.bookspine.readinglog.domain.model.ThemeMode
import com.bookspine.readinglog.presentation.app.AppRoot
import com.bookspine.readinglog.presentation.app.AppViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        setContent {
            val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.instance)
            val mode by appViewModel.themeMode.collectAsStateWithLifecycle()
            AppTheme(darkTheme = mode == ThemeMode.FOREST) {
                AppRoot(appViewModel = appViewModel)
            }
        }
    }
}
