package io.github.airflux.reader

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.path.JsPath
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TraversableReaderTest {

    companion object {
        val stringReader = JsReader { input -> input.readAsString() }
    }

    @Test
    fun `Testing 'traversable' function`() {
        val json: JsValue = JsArray(
            JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
        )

        val result: JsResult<List<String>> = JsReader.traversable(
            input = json,
            reader = stringReader,
            factory = CollectionBuilderFactory.listFactory()
        )

        result as JsResult.Success
        assertEquals(JsPath.empty, result.path)
        assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
    }

    @Test
    fun `Testing 'traversable' function (invalid type)`() {
        val json: JsValue = JsObject(
            "user" to JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
        )

        val result: JsResult<List<String>> = JsReader.traversable(
            input = json,
            reader = stringReader,
            factory = CollectionBuilderFactory.listFactory()
        )

        result as JsResult.Failure
        assertEquals(1, result.errors.size)

        result.errors[0]
            .also { (pathError, errors) ->
                assertEquals(JsPath.empty, pathError)

                assertEquals(1, errors.size)
                val error = errors[0] as JsError.InvalidType
                assertEquals(JsValue.Type.ARRAY, error.expected)
                assertEquals(JsValue.Type.OBJECT, error.actual)
            }
    }

    @Test
    fun `Testing 'traversable' function (collection with inconsistent content)`() {
        val json: JsValue = JsArray(
            JsString(FIRST_PHONE_VALUE),
            JsNumber.valueOf(10),
            JsBoolean.True,
            JsString(SECOND_PHONE_VALUE),
        )

        val result: JsResult<List<String>> = JsReader.traversable(
            input = json,
            reader = stringReader,
            factory = CollectionBuilderFactory.listFactory()
        )

        result as JsResult.Failure
        assertEquals(2, result.errors.size)

        result.errors[0]
            .also { (pathError, errors) ->
                assertEquals(JsPath.empty / 1, pathError)
                assertEquals(1, errors.size)
            }

        result.errors[1]
            .also { (pathError, errors) ->
                assertEquals(JsPath.empty / 2, pathError)
                assertEquals(1, errors.size)
            }
    }

    @Test
    fun `Testing 'traversable' function (array is empty)`() {
        val json: JsValue = JsArray<JsString>()

        val result: JsResult<List<String>> = JsReader.traversable(
            input = json,
            reader = stringReader,
            factory = CollectionBuilderFactory.listFactory()
        )

        result as JsResult.Success
        assertEquals(JsPath.empty, result.path)
        assertEquals(listOf(), result.value)
    }
}
