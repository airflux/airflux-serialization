package io.github.airflux.common

import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import kotlin.test.assertEquals

fun <T> JsResult<T?>.assertAsSuccess(path: JsPath, value: T?) {
    this as JsResult.Success
    assertEquals(expected = path, actual = this.path)
    assertEquals(expected = value, actual = this.value)
}

fun JsResult<*>.assertAsFailure(vararg expected: Pair<JsPath, List<JsError>>) {

    val failures = (this as JsResult.Failure).errors

    assertEquals(expected = expected.size, actual = failures.size, message = "Failures more than expected.")

    expected.forEachIndexed { index, (path, errors) ->
        assertEquals(expected = path, actual = failures[index].first, message = "The path of the failure is not as expected.")
        assertEquals(
            expected = errors.size,
            actual = failures[index].second.size,
            message = "Errors by path '$path' more than expected."
        )

        errors.forEachIndexed { errorIndex, error ->
            assertEquals(
                expected = error,
                actual = failures[index].second[errorIndex],
                message = "The error is not expected."
            )
        }
    }
}
