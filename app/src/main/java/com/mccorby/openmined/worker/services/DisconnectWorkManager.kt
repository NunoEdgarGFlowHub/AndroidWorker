package com.mccorby.openmined.worker.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.openmined.worker.domain.SyftRepository

class DisconnectWorkManager(
    private val syftRepository: SyftRepository,
    ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {

    override fun doWork(): Result {
        syftRepository.disconnect()
        return Result.SUCCESS
    }
}
