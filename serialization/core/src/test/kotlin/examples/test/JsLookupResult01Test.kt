// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.test

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.knit.test.captureOutput

internal class JsLookupResult01Test : AnnotationSpec() {
    @Test
    fun testExampleLookupResult01() {
        val result = captureOutput("ExampleLookupResult01") { examples.exampleLookupResult01.main() }
        result shouldContainExactly listOf(
            "Defined(location=#/user/phone, value=JsString(123))"
        )
    }
}
