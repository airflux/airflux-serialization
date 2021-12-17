package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.ValueCastErrorBuilder
import io.github.airflux.reader.result.asFailure
import io.github.airflux.reader.result.asSuccess
import io.github.airflux.value.extension.readAsBoolean
import io.github.airflux.value.extension.readAsNumber
import io.github.airflux.value.extension.readAsString
import java.math.BigDecimal

@Suppress("unused")
object BasePrimitiveReader {

    /**
     * Reader for primitive [Boolean] type.
     */
    fun boolean(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<Boolean> =
        JsReader { _, location, input -> input.readAsBoolean(location, invalidTypeErrorBuilder) }

    /**
     * Reader for primitive [String] type.
     */
    fun string(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<String> =
        JsReader { _, location, input -> input.readAsString(location, invalidTypeErrorBuilder) }

    /**
     * Reader for primitive [Byte] type.
     */
    fun byte(
        invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
        valueCastErrorBuilder: ValueCastErrorBuilder
    ): JsReader<Byte> =
        JsReader { _, location, input ->
            input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
                try {
                    text.toByte().asSuccess(location = p)
                } catch (expected: NumberFormatException) {
                    valueCastErrorBuilder.build(text, Byte::class).asFailure(location = p)
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
        JsReader { _, location, input ->
            input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
                try {
                    text.toShort().asSuccess(location = p)
                } catch (expected: NumberFormatException) {
                    valueCastErrorBuilder.build(text, Short::class).asFailure(location = p)
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
        JsReader { _, location, input ->
            input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
                try {
                    text.toInt().asSuccess(location = p)
                } catch (expected: NumberFormatException) {
                    valueCastErrorBuilder.build(text, Int::class).asFailure(location = p)
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
        JsReader { _, location, input ->
            input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
                try {
                    text.toLong().asSuccess(location = p)
                } catch (expected: NumberFormatException) {
                    valueCastErrorBuilder.build(text, Long::class).asFailure(location = p)
                }
            }
        }

    /**
     * Reader for [BigDecimal] type.
     */
    fun bigDecimal(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<BigDecimal> =
        JsReader { _, location, input ->
            input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
                BigDecimal(text).asSuccess(location = p)
            }
        }
}
