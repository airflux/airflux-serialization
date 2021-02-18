package io.github.airflux.dsl

import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsResult
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
        assertEquals(JsPath.empty, result.path)
        assertEquals(USER_NAME_VALUE, result.value)
    }
}
