package io.github.airflux.dsl

import io.github.airflux.common.TestData.DEFAULT_PHONE_VALUE
import io.github.airflux.common.TestData.DEFAULT_USER_NAME_VALUE
import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.dsl.PathDsl.div
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ReaderDslTest {

    companion object {
        private val stringReader = JsReader { input -> input.readAsString() }
    }

    @Test
    fun `Testing of a read from JsValue using reader`() {
        val json: JsValue = JsString(USER_NAME_VALUE)

        val result = ReaderDsl.read(from = json, using = stringReader)

        result as JsResult.Success
        assertEquals(JsPath(), result.path)
        assertEquals(USER_NAME_VALUE, result.value)
    }

    @Test
    fun `Testing of a read a required attribute by name`() {
        val json: JsValue = JsObject(
            "name" to JsString(USER_NAME_VALUE)
        )
        val pathReader: JsReader<String> = ReaderDsl.readRequired(byName = "name", using = stringReader)

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(JsPath("name"), result.path)
        assertEquals(USER_NAME_VALUE, result.value)
    }

    @Test
    fun `Testing of a read a required attribute by path`() {
        val json: JsValue = JsObject(
            "user" to JsObject(
                "phones" to JsArray(
                    JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                )
            )
        )
        val path = "user" / "phones" / 0
        val pathReader: JsReader<String> = ReaderDsl.readRequired(byPath = path, using = stringReader)

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(path, result.path)
        assertEquals(FIRST_PHONE_VALUE, result.value)
    }

    @Test
    fun `Testing of a read an attribute with a default value by name`() {
        val json: JsValue = JsObject(
            "name" to JsString(USER_NAME_VALUE)
        )
        val pathReader: JsReader<String> =
            ReaderDsl.readOrDefault(byName = "name", using = stringReader, defaultValue = { DEFAULT_USER_NAME_VALUE })

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(JsPath("name"), result.path)
        assertEquals(USER_NAME_VALUE, result.value)
    }

    @Test
    fun `Testing of a read an attribute with a default value by path`() {
        val json: JsValue = JsObject(
            "user" to JsObject(
                "phones" to JsArray(
                    JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                )
            )
        )
        val path = "user" / "phones" / 0
        val pathReader: JsReader<String> =
            ReaderDsl.readOrDefault(byPath = path, using = stringReader, defaultValue = { DEFAULT_PHONE_VALUE })

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(path, result.path)
        assertEquals(FIRST_PHONE_VALUE, result.value)
    }

    @Test
    fun `Testing of a read an optional attribute by name`() {
        val json: JsValue = JsObject(
            "name" to JsString(USER_NAME_VALUE)
        )
        val pathReader: JsReader<String?> = ReaderDsl.readNullable(byName = "name", using = stringReader)

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(JsPath("name"), result.path)
        assertEquals(USER_NAME_VALUE, result.value)
    }

    @Test
    fun `Testing of a read an optional attribute by path`() {
        val json: JsValue = JsObject(
            "user" to JsObject(
                "phones" to JsArray(
                    JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                )
            )
        )
        val path = "user" / "phones" / 0
        val pathReader: JsReader<String?> = ReaderDsl.readNullable(byPath = path, using = stringReader)

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(path, result.path)
        assertEquals(FIRST_PHONE_VALUE, result.value)
    }

    @Test
    fun `Testing of a read an optional attribute with a default value by name`() {
        val json: JsValue = JsObject(
            "name" to JsString(USER_NAME_VALUE)
        )
        val pathReader: JsReader<String?> =
            ReaderDsl.readNullableOrDefault(byName = "name", using = stringReader, defaultValue = { DEFAULT_USER_NAME_VALUE })

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(JsPath("name"), result.path)
        assertEquals(USER_NAME_VALUE, result.value)
    }

    @Test
    fun `Testing of a read an optional attribute with a default value by path`() {
        val json: JsValue = JsObject(
            "user" to JsObject(
                "phones" to JsArray(
                    JsString(FIRST_PHONE_VALUE)
                )
            )
        )
        val path = "user" / "phones" / 0
        val pathReader: JsReader<String?> =
            ReaderDsl.readNullableOrDefault(byPath = path, using = stringReader, defaultValue = { SECOND_PHONE_VALUE })

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(path, result.path)
        assertEquals(FIRST_PHONE_VALUE, result.value)
    }

    @Test
    fun `Testing of a read a traversable attribute by name as the list`() {
        val json: JsValue = JsObject(
            "phones" to JsArray(
                JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
            )
        )
        val pathReader: JsReader<List<String>> = ReaderDsl.readTraversable(byName = "phones", using = stringReader)

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(JsPath("phones"), result.path)
        assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
    }

    @Test
    fun `Testing of a read a traversable attribute by path as the list`() {
        val json: JsValue = JsObject(
            "user" to JsObject(
                "phones" to JsArray(
                    JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                )
            )
        )
        val path = "user" / "phones"
        val pathReader: JsReader<List<String>> = ReaderDsl.readTraversable(byPath = path, using = stringReader)

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(path, result.path)
        assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
    }

    @Test
    fun `Testing of a read a traversable attribute by name as the set`() {
        val json: JsValue = JsObject(
            "phones" to JsArray(
                JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
            )
        )
        val pathReader: JsReader<Set<String>> =
            ReaderDsl.readTraversable(
                factory = CollectionBuilderFactory.setFactory(),
                byName = "phones",
                using = stringReader
            )

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(JsPath("phones"), result.path)
        assertEquals(setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
    }

    @Test
    fun `Testing of a read a traversable attribute by path as the set`() {
        val json: JsValue = JsObject(
            "user" to JsObject(
                "phones" to JsArray(
                    JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
                )
            )
        )
        val path = "user" / "phones"
        val pathReader: JsReader<Set<String>> =
            ReaderDsl.readTraversable(
                factory = CollectionBuilderFactory.setFactory(),
                byPath = path,
                using = stringReader
            )

        val result = pathReader.read(json)

        result as JsResult.Success
        assertEquals(path, result.path)
        assertEquals(setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
    }
}
