package org.openmined.worker.domain.usecase

import org.openmined.worker.domain.MLFramework
import org.openmined.worker.domain.SyftCommand
import org.openmined.worker.domain.SyftMessage
import org.openmined.worker.domain.SyftOperand
import org.openmined.worker.domain.SyftRepository
import org.openmined.worker.domain.SyftResult

class ExecuteCommandUseCase(private val syftRepository: SyftRepository, private val mlFramework: MLFramework) {

    operator fun invoke(executeCommandMessage: SyftMessage.ExecuteCommand): SyftResult.CommandResult {
        val result = createCommandEvent(executeCommandMessage)
        return SyftResult.CommandResult(executeCommandMessage.command, result)
    }

    private fun createCommandEvent(syftMessage: SyftMessage.ExecuteCommand): SyftOperand.SyftTensor {
        return when (syftMessage.command) {
            is SyftCommand.Add -> {
                processAdd(syftMessage.command)
            }
            is SyftCommand.Multiply -> {
                processMultiply(syftMessage.command)
            }
        }
    }

    private fun processMultiply(command: SyftCommand.Multiply): SyftOperand.SyftTensor {
        val result = when (command.tensors[0]) {
            is SyftOperand.SyftTensor -> {
                mlFramework.add(
                    command.tensors[0] as SyftOperand.SyftTensor,
                    command.tensors[1] as SyftOperand.SyftTensor
                )
            }
            is SyftOperand.SyftTensorPointer -> {
                mlFramework.multiply(
                    syftRepository.getObject(command.tensors[0].id),
                    syftRepository.getObject(command.tensors[1].id)
                )
            }
        }
        // Add only expects now a single return id
        val resultId = command.resultIds[0]
        syftRepository.setObject(resultId, result)
        syftRepository.sendMessage(SyftMessage.OperationAck)
        return result
    }

    private fun processAdd(command: SyftCommand.Add): SyftOperand.SyftTensor {
        val result = when (command.tensors[0]) {
            is SyftOperand.SyftTensor -> {
                mlFramework.add(
                    command.tensors[0] as SyftOperand.SyftTensor,
                    command.tensors[1] as SyftOperand.SyftTensor
                )
            }
            is SyftOperand.SyftTensorPointer -> {
                mlFramework.add(
                    syftRepository.getObject(command.tensors[0].id),
                    syftRepository.getObject(command.tensors[1].id)
                )
            }
        }
        // Add only expects now a single return id
        val resultId = command.resultIds[0]
        syftRepository.setObject(resultId, result)
        syftRepository.sendMessage(SyftMessage.OperationAck)
        return result
    }
}