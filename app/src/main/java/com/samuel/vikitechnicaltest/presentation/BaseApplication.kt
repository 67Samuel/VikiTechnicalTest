package com.samuel.vikitechnicaltest.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Required by Hilt
 * https://dagger.dev/hilt/application
 */
@HiltAndroidApp
class BaseApplication : Application()