package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.ValueCastErrorBuilder
import io.github.airflux.reader.extension.readAsBoolean
import io.github.airflux.reader.extension.readAsNumber
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.asFailure
import io.github.airflux.reader.result.asSuccess
import java.math.BigDecimal

@Suppress("unused")
interface BasePrimitiveReader {

    companion object {

        /**
         * Reader for primitive [Boolean] type.
         */
        fun boolean(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<Boolean> =
            JsReader { input -> input.readAsBoolean(invalidTypeErrorBuilder) }

        /**
         * Reader for primitive [String] type.
         */
        fun string(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<String> =
            JsReader { input -> input.readAsString(invalidTypeErrorBuilder) }

        /**
         * Reader for primitive [Byte] type.
         */
        fun byte(
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
            valueCastErrorBuilder: ValueCastErrorBuilder
        ): JsReader<Byte> =
            JsReader { input ->
                input.readAsNumber(invalidTypeErrorBuilder) { text ->
                    try {
                        text.toByte().asSuccess()
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Byte::class).asFailure()
                    }
                }
            }

        /**
         * Reader for primitive [Short] type.
         */
        fun short(
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
            valueCastErrorBuilder: ValueCastErrorBuilder
        ): JsReader<Short> =
            JsReader { input ->
                input.readAsNumber(invalidTypeErrorBuilder) { text ->
                    try {
                        text.toShort().asSuccess()
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Short::class).asFailure()
                    }
                }
            }

        /**
         * Reader for primitive [Int] type.
         */
        fun int(
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
            valueCastErrorBuilder: ValueCastErrorBuilder
        ): JsReader<Int> =
            JsReader { input ->
                input.readAsNumber(invalidTypeErrorBuilder) { text ->
                    try {
                        text.toInt().asSuccess()
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Int::class).asFailure()
                    }
                }
            }

        /**
         * Reader for primitive [Long] type.
         */
        fun long(
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
            valueCastErrorBuilder: ValueCastErrorBuilder
        ): JsReader<Long> =
            JsReader { input ->
                input.readAsNumber(invalidTypeErrorBuilder) { text ->
                    try {
                        text.toLong().asSuccess()
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Long::class).asFailure()
                    }
                }
            }

        /**
         * Reader for [BigDecimal] type.
         */
        fun bigDecimal(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<BigDecimal> =
            JsReader { input ->
                input.readAsNumber(invalidTypeErrorBuilder) { text ->
                    BigDecimal(text).asSuccess()
                }
            }
    }
}
