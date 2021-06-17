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
            JsReader { _, path, input -> input.readAsBoolean(path, invalidTypeErrorBuilder) }

        /**
         * Reader for primitive [String] type.
         */
        fun string(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<String> =
            JsReader { _, path, input -> input.readAsString(path, invalidTypeErrorBuilder) }

        /**
         * Reader for primitive [Byte] type.
         */
        fun byte(
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
            valueCastErrorBuilder: ValueCastErrorBuilder
        ): JsReader<Byte> =
            JsReader { _, path, input ->
                input.readAsNumber(path, invalidTypeErrorBuilder) { p, text ->
                    try {
                        text.toByte().asSuccess(path = p)
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Byte::class).asFailure(path = p)
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
            JsReader { _, path, input ->
                input.readAsNumber(path, invalidTypeErrorBuilder) { p, text ->
                    try {
                        text.toShort().asSuccess(path = p)
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Short::class).asFailure(path = p)
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
            JsReader { _, path, input ->
                input.readAsNumber(path, invalidTypeErrorBuilder) { p, text ->
                    try {
                        text.toInt().asSuccess(path = p)
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Int::class).asFailure(path = p)
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
            JsReader { _, path, input ->
                input.readAsNumber(path, invalidTypeErrorBuilder) { p, text ->
                    try {
                        text.toLong().asSuccess(path = p)
                    } catch (expected: NumberFormatException) {
                        valueCastErrorBuilder.build(text, Long::class).asFailure(path = p)
                    }
                }
            }

        /**
         * Reader for [BigDecimal] type.
         */
        fun bigDecimal(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<BigDecimal> =
            JsReader { _, path, input ->
                input.readAsNumber(path, invalidTypeErrorBuilder) { p, text ->
                    BigDecimal(text).asSuccess(path = p)
                }
            }
    }
}
