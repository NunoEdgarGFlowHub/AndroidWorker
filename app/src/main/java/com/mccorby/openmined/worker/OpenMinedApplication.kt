package com.mccorby.openmined.worker

import com.mccorby.openmined.worker.di.ApplicationComponent
import android.app.Application
import com.mccorby.openmined.worker.di.DaggerApplicationComponent
import com.mccorby.openmined.worker.mlframework.di.DaggerMLFrameworkComponent

class OpenMinedApplication : Application() {

    // Reference to the application graph that is used across the whole app
    val appComponent: ApplicationComponent = DaggerApplicationComponent
        .builder()
        .mLFrameworkComponent(DaggerMLFrameworkComponent.create())
        .build()
}
