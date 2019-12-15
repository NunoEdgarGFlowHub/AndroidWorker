package org.openmined.worker.domain.usecase

import org.openmined.worker.domain.SyftMessage
import org.openmined.worker.domain.SyftRepository
import org.openmined.worker.domain.SyftResult

class GetObjectUseCase(private val syftRepository: SyftRepository) {

    operator fun invoke(syftMessage: SyftMessage.GetObject): SyftResult.ObjectRetrieved {
        val tensor = syftRepository.getObject(syftMessage.tensorPointerId)
        // TODO copy should not be necessary. Here set just to make it work. This is a value that should have been already set before
        val tensorCopy = tensor.copy(id = syftMessage.tensorPointerId)
        syftRepository.sendMessage(SyftMessage.RespondToObjectRequest(tensorCopy))
        return SyftResult.ObjectRetrieved(tensorCopy)
    }
}