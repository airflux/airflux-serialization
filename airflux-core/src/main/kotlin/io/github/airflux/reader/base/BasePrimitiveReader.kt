package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsBoolean
import io.github.airflux.reader.extension.readAsNumber
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.asFailure
import io.github.airflux.reader.result.asSuccess
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
                        text.toByte().asSuccess()
                    } catch (expected: NumberFormatException) {
                        errorValueCast(text, Byte::class).asFailure()
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
                        text.toShort().asSuccess()
                    } catch (expected: NumberFormatException) {
                        errorValueCast(text, Short::class).asFailure()
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
                        text.toInt().asSuccess()
                    } catch (expected: NumberFormatException) {
                        errorValueCast(text, Int::class).asFailure()
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
                        text.toLong().asSuccess()
                    } catch (expected: NumberFormatException) {
                        errorValueCast(text, Long::class).asFailure()
                    }
                }
            }

        /**
         * Reader for [BigDecimal] type.
         */
        fun bigDecimal(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError): JsReader<BigDecimal> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    BigDecimal(text).asSuccess()
                }
            }
    }
}
