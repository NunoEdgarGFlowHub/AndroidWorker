package org.openmined.worker.datasource.mapper

import org.openmined.worker.domain.SyftMessage
import org.openmined.worker.domain.SyftOperand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import org.msgpack.value.impl.ImmutableArrayValueImpl
import org.msgpack.value.impl.ImmutableLongValueImpl
import org.msgpack.value.impl.ImmutableStringValueImpl

class MappersKtTest {

    @Test
    fun `Given a msgpack byte array containing a Set Object operation and a tensor the mapper returns the corresponding SyftMessage`() {
        // [2,[2,[0,[27548798307,"the_binary_stuff",null,null,null,null]]]]
        // (27, (2, (13, (37069756572, b'<TENSOR>', None, None, None, None))))

        // Given
        val tensorAsBytes = ByteArray(0)
        val tensorId = 6830L
        val expected = SyftMessage.SetObject(
            SyftOperand.SyftTensor(
                tensorId,
                tensorAsBytes.contentToString().toByteArray()
            )
        )

        val tensorArray = packTensor(tensorId, tensorAsBytes)

        val operationArray = packSendObject(packTuple(tensorArray))

        val packer = MessagePack.newDefaultBufferPacker()
        packer.packValue(ImmutableArrayValueImpl(arrayOf(operationArray)))

        // When
        val syftMessage = packer.toByteArray().mapToSyftMessage()

        // Then
        assertTrue(syftMessage is SyftMessage.SetObject)
        assertEquals(expected, syftMessage)
    }

    @Test
    fun `Given a msgpack byte array containing an Add operation and two pointers the mapper returns the corresponding SyftMessage`() {
        // NOTE Removing. These tests have a strong dependency on msgpack. The whole serde system is being refactored in PySyft
        // which will cause this expensive-to-maintain test useless
//        // [2,[1,[2,[[2,["__add__",[11,[35056008441,73609023527,0,null,[5]]],[2,[[11,[5320203372,93504664205,0,null,[5]]]]],[5,{}]]],[3,[34402560415]]]]]]
//        // (26, (1, ((6, ((5, (b'__add__',)), (20, (68150413139, 18881022525, 'alice', None, (10, (5,)), True)), (6, ((20, (50949323355, 8049055689, 'alice', None, (10, (5,)), True)),)), (0, ()))), (17547319586,))))
//        // Add is a CMD with the first operand and a list of other operands
//        val tensor1Id = 6830L
//        val tensor2Id = 1234L
//        val tensorPointer1 = SyftOperand.SyftTensorPointer(tensor1Id)
//        val tensorPointer2 = SyftOperand.SyftTensorPointer(tensor2Id)
//        val resultId = 7766L
//        val resultIds = listOf(resultId)
//        val expected = SyftMessage.ExecuteCommand(SyftCommand.Add(listOf(tensorPointer1, tensorPointer2), resultIds))
//
//        val pointer1 = packTensorPointer(tensor1Id, 9999)
//
//        val pointer2 = ImmutableArrayValueImpl(arrayOf<Value>(packTensorPointer(tensor2Id, 9999)))
//
//        // [2,[[11,[9999,1234]]]]
//        val secondOperands = packTuple(pointer2)
//
//        val operation = packOperation(CommandConstants.CMD_ADD, pointer1, secondOperands)
//
//        val operationAndOperandsArray = packTuple(operation)
//
//        val resultIdsWrapper = ImmutableArrayValueImpl(
//            arrayOf<Value>(
//                ImmutableLongValueImpl(TypeConstants.TYPE_LIST.toLong()),
//                ImmutableArrayValueImpl(arrayOf<Value>(ImmutableLongValueImpl(resultId)))
//            )
//        )
//
//        val operationAndResultWrapper =
//            packTuple(ImmutableArrayValueImpl(arrayOf<Value>(operationAndOperandsArray, resultIdsWrapper)))
//
//        val operationArray = ImmutableArrayValueImpl(
//            arrayOf<Value>(
//                ImmutableLongValueImpl(OperationConstants.CMD.toLong()), // CMD
//                operationAndResultWrapper
//            )
//        )
//
//        val outerWrapper = ImmutableArrayValueImpl(
//            arrayOf<Value>(
//                ImmutableLongValueImpl(TypeConstants.TYPE_TUPLE.toLong()),
//                operationArray
//            )
//        )
//
//        val packer = MessagePack.newDefaultBufferPacker()
//        packer.packValue(ImmutableArrayValueImpl(arrayOf(outerWrapper)))
//
//        // When
//        val syftMessage = packer.toByteArray().mapToSyftMessage()
//
//        // Then
//        assertTrue(syftMessage is SyftMessage.ExecuteCommand)
//        assertEquals(expected, syftMessage)
    }

    @Test
    fun `Given a msgpack byte array containing a ForceDelete operation the mapper returns the corresponding SyftMessage`() {
        // [38,93504664205]
        // Given
        val pointerId = 1234L
        val expected = SyftMessage.DeleteObject(pointerId)

        val operation = ImmutableArrayValueImpl(
            arrayOf<Value>(
                ImmutableLongValueImpl(OperationConstants.FORCE_OBJ_DEL.toLong()),
                ImmutableLongValueImpl(pointerId)
            )
        )

        val packer = MessagePack.newDefaultBufferPacker()
        packer.packValue(ImmutableArrayValueImpl(arrayOf(operation)))

        // When
        val syftMessage = packer.toByteArray().mapToSyftMessage()

        // Then
        assertTrue(syftMessage is SyftMessage.DeleteObject)
        assertEquals(expected, syftMessage)
    }

    @Test
    fun `Given a msgpack byte array containing an Get operation the mapper returns the corresponding SyftMessage`() {
        // (28, (3, 17547319586))
        // Given
        val pointerId = 1234L
        val expected = SyftMessage.GetObject(pointerId)

        val operation = ImmutableArrayValueImpl(
            arrayOf<Value>(
                ImmutableLongValueImpl(OperationConstants.OBJ_REQ.toLong()),
                ImmutableArrayValueImpl(
                    arrayOf<Value>(
                        ImmutableLongValueImpl(TypeConstants.TYPE_TUPLE.toLong()),
                        ImmutableLongValueImpl(pointerId)
                    )
                )
            )
        )

        val packer = MessagePack.newDefaultBufferPacker()
        packer.packValue(ImmutableArrayValueImpl(arrayOf(operation)))

        // When
        val syftMessage = packer.toByteArray().mapToSyftMessage()

        // Then
        assertTrue(syftMessage is SyftMessage.GetObject)
        assertEquals(expected, syftMessage)
    }

    private fun packOperation(

        operation: String,
        pointer: ImmutableArrayValueImpl,
        secondOperands: ImmutableArrayValueImpl
    ): ImmutableArrayValueImpl {
        // [18,["__add__"]]
        return ImmutableArrayValueImpl(
            arrayOf<Value>(
                ImmutableArrayValueImpl(
                    arrayOf<Value>(
                        ImmutableLongValueImpl(26),
                        ImmutableArrayValueImpl(arrayOf(ImmutableStringValueImpl(operation)))
                    )
                ),
                pointer,
                secondOperands
            )
        )
    }

    private fun packTensorPointer(tensorId: Long, originTensorId: Long): ImmutableArrayValueImpl {
        // [11,[35056008441,73609023527,0,null,[5]]]
        return ImmutableArrayValueImpl(
            arrayOf<Value>(
                ImmutableLongValueImpl(TypeConstants.TYPE_TENSOR_POINTER.toLong()), // Type Pointer
                ImmutableArrayValueImpl(
                    arrayOf<Value>(
                        ImmutableLongValueImpl(originTensorId),
                        ImmutableLongValueImpl(tensorId)
                    )
                )
            )
        )
    }

    private fun packTensor(tensorId: Long, tensorAsBytes: ByteArray): ImmutableArrayValueImpl {
        return ImmutableArrayValueImpl(
            arrayOf<Value>(
                ImmutableLongValueImpl(TypeConstants.TYPE_TENSOR.toLong()),
                ImmutableArrayValueImpl(
                    arrayOf<Value>(
                        ImmutableLongValueImpl(tensorId),
                        ImmutableStringValueImpl(tensorAsBytes.contentToString())
                    )
                )
            )
        )
    }

    private fun packSendObject(tensorArray: ImmutableArrayValueImpl): ImmutableArrayValueImpl {
        return ImmutableArrayValueImpl(
            arrayOf<Value>(
                ImmutableLongValueImpl(OperationConstants.OBJ.toLong()),
                tensorArray
            )
        )
    }

    private fun packTuple(operationArray: ImmutableArrayValueImpl): ImmutableArrayValueImpl {
        return ImmutableArrayValueImpl(
            arrayOf<Value>(
                ImmutableLongValueImpl(TypeConstants.TYPE_TUPLE.toLong()),
                operationArray
            )
        )
    }
}