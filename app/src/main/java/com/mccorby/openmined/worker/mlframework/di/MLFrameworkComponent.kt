package com.mccorby.openmined.worker.mlframework.di

import com.mccorby.openmined.worker.framework.di.MLFrameworkModule
import dagger.Component
import org.openmined.worker.domain.MLFramework

@Component(
    modules = [MLFrameworkModule::class]
)
abstract class MLFrameworkComponent {
    abstract fun mlFramework(): MLFramework
}