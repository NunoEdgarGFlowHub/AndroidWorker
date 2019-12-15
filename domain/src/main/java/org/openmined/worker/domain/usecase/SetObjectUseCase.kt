package org.openmined.worker.domain.usecase

import org.openmined.worker.domain.SyftMessage
import org.openmined.worker.domain.SyftOperand
import org.openmined.worker.domain.SyftRepository
import org.openmined.worker.domain.SyftResult

class SetObjectUseCase(private val syftRepository: SyftRepository) {

    operator fun invoke(syftMessage: SyftMessage.SetObject): SyftResult.ObjectAdded {
        syftRepository.setObject(syftMessage.objectToSet as SyftOperand.SyftTensor)
        syftRepository.sendMessage(SyftMessage.OperationAck)
        return SyftResult.ObjectAdded(syftMessage.objectToSet)
    }
}