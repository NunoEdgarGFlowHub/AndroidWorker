package org.openmined.worker.domain.usecase

import org.openmined.worker.domain.SyftMessage
import org.openmined.worker.domain.SyftRepository
import org.openmined.worker.domain.SyftResult

class DeleteObjectUseCase(private val syftRepository: SyftRepository) {

    operator fun invoke(newSyftMessage: SyftMessage.DeleteObject): SyftResult.ObjectRemoved {
        syftRepository.removeObject(newSyftMessage.objectToDelete)
        syftRepository.sendMessage(SyftMessage.OperationAck)
        return SyftResult.ObjectRemoved(newSyftMessage.objectToDelete)
    }
}