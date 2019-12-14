package com.mccorby.openmined.worker.framework.di

import com.mccorby.openmined.worker.domain.MLFramework
import dagger.Component

@Component(
    modules = [MLFrameworkModule::class]
)
abstract class MLFrameworkComponent {
    abstract fun mlFramework(): MLFramework
}