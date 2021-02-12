package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsBoolean
import io.github.airflux.reader.extension.readAsNumber
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import java.math.BigDecimal

@Suppress("unused")
object BasePrimitiveReader {
    /**
     * Reader for primitive [Boolean] type.
     */
    val boolean: JsReader<Boolean> = JsReader { input -> input.readAsBoolean() }

    /**
     * Reader for primitive [String] type.
     */
    val string: JsReader<String> = JsReader { input -> input.readAsString() }

    /**
     * Reader for primitive [Byte] type.
     */
    val byte: JsReader<Byte> = JsReader { input ->
        input.readAsNumber { text ->
            try {
                JsResult.Success(text.toByte())
            } catch (expected: NumberFormatException) {
                JsResult.Failure(error = JsError.ValueCast(value = text, type = Byte::class))
            }
        }
    }

    /**
     * Reader for primitive [Short] type.
     */
    val short: JsReader<Short> = JsReader { input ->
        input.readAsNumber { text ->
            try {
                JsResult.Success(text.toShort())
            } catch (expected: NumberFormatException) {
                JsResult.Failure(error = JsError.ValueCast(value = text, type = Short::class))
            }
        }
    }

    /**
     * Reader for primitive [Int] type.
     */
    val int: JsReader<Int> = JsReader { input ->
        input.readAsNumber { text ->
            try {
                JsResult.Success(text.toInt())
            } catch (expected: NumberFormatException) {
                JsResult.Failure(error = JsError.ValueCast(value = text, type = Int::class))
            }
        }
    }

    /**
     * Reader for primitive [Long] type.
     */
    val long: JsReader<Long> = JsReader { input ->
        input.readAsNumber { text ->
            try {
                JsResult.Success(text.toLong())
            } catch (expected: NumberFormatException) {
                JsResult.Failure(error = JsError.ValueCast(value = text, type = Long::class))
            }
        }
    }

    /**
     * Reader for [BigDecimal] type.
     */
    val bigDecimal: JsReader<BigDecimal> = JsReader { input ->
        input.readAsNumber { text ->
            JsResult.Success(BigDecimal(text))
        }
    }
}
