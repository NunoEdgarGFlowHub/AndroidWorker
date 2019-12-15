package com.mccorby.openmined.worker.di

import com.mccorby.openmined.worker.mlframework.di.MLFrameworkComponent
import com.mccorby.openmined.worker.ui.MainActivity
import dagger.Component

@Component(
    dependencies = [MLFrameworkComponent::class]
)
interface ApplicationComponent {
    fun inject(activity: MainActivity)
}