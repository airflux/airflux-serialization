package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsBoolean
import io.github.airflux.reader.extension.readAsNumber
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
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
        fun <E : JsError> boolean(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E
        ): JsReader<Boolean, E> =
            JsReader { input -> input.readAsBoolean(errorInvalidType) }

        /**
         * Reader for primitive [String] type.
         */
        fun <E : JsError> string(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E
        ): JsReader<String, E> =
            JsReader { input -> input.readAsString(errorInvalidType) }

        /**
         * Reader for primitive [Byte] type.
         */
        fun <E : JsError> byte(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E,
            errorValueCast: (value: String, type: KClass<*>) -> E
        ): JsReader<Byte, E> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        text.toByte().asSuccess()
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure<E>(errorValueCast(text, Byte::class))
                    }
                }
            }

        /**
         * Reader for primitive [Short] type.
         */
        fun <E : JsError> short(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E,
            errorValueCast: (value: String, type: KClass<*>) -> E
        ): JsReader<Short, E> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        text.toShort().asSuccess()
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure<E>(errorValueCast(text, Short::class))
                    }
                }
            }

        /**
         * Reader for primitive [Int] type.
         */
        fun <E : JsError> int(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E,
            errorValueCast: (value: String, type: KClass<*>) -> E
        ): JsReader<Int, E> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        text.toInt().asSuccess()
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure<E>(errorValueCast(text, Int::class))
                    }
                }
            }

        /**
         * Reader for primitive [Long] type.
         */
        fun <E : JsError> long(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E,
            errorValueCast: (value: String, type: KClass<*>) -> E
        ): JsReader<Long, E> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    try {
                        text.toLong().asSuccess()
                    } catch (expected: NumberFormatException) {
                        JsResult.Failure<E>(errorValueCast(text, Long::class))
                    }
                }
            }

        /**
         * Reader for [BigDecimal] type.
         */
        fun <E : JsError> bigDecimal(
            errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E
        ): JsReader<BigDecimal, E> =
            JsReader { input ->
                input.readAsNumber(errorInvalidType) { text ->
                    BigDecimal(text).asSuccess()
                }
            }
    }
}
