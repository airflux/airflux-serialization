package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsBoolean
import io.github.airflux.reader.extension.readAsNumber
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue
import java.math.BigDecimal
import kotlin.reflect.KClass

@Suppress("unused")
interface BasePrimitiveReader {

    companion object {

        /**
         * Reader for primitive [Boolean] type.
         */
        fun boolean(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError): JsReader<Boolean> =
            JsReader { input -> input.readAsBoolean(errorInvalidType) }

        /**
         * Reader for primitive [String] type.
         */
        fun string(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError): JsReader<String> =
            JsReader { input -> input.readAsString(errorInvalidType) }

        /**
         * Reader for primitive [Byte] type.
         */
        fun byte(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError,
            errorValueCast: (value: String, type: KClass<*>) -> JsError
        ): JsReader<Byte> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        JsResult.Success(text.toByte())
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure(error = errorValueCast(text, Byte::class))
                    }
                }
            }

        /**
         * Reader for primitive [Short] type.
         */
        fun short(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError,
            errorValueCast: (value: String, type: KClass<*>) -> JsError
        ): JsReader<Short> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        JsResult.Success(text.toShort())
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure(error = errorValueCast(text, Short::class))
                    }
                }
            }

        /**
         * Reader for primitive [Int] type.
         */
        fun int(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError,
            errorValueCast: (value: String, type: KClass<*>) -> JsError
        ): JsReader<Int> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        JsResult.Success(text.toInt())
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure(error = errorValueCast(text, Int::class))
                    }
                }
            }

        /**
         * Reader for primitive [Long] type.
         */
        fun long(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError,
            errorValueCast: (value: String, type: KClass<*>) -> JsError
        ): JsReader<Long> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        JsResult.Success(text.toLong())
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure(error = errorValueCast(text, Long::class))
                    }
                }
            }

        /**
         * Reader for [BigDecimal] type.
         */
        fun bigDecimal(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError): JsReader<BigDecimal> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    JsResult.Success(BigDecimal(text))
                }
            }
    }
}
