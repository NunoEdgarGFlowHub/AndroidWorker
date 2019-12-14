package com.mccorby.openmined.worker.framework.di

import com.mccorby.openmined.worker.domain.MLFramework
import com.mccorby.openmined.worker.framework.DL4JFramework
import dagger.Binds
import dagger.Module

@Module
internal abstract class MLFrameworkModule {

    @Binds
    abstract fun bindMLFramework(mlFramework: DL4JFramework): MLFramework
}