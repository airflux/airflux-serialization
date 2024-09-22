// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.test

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.knit.test.captureOutput

internal class JsLookupResult02Test : AnnotationSpec() {
    @Test
    fun testExampleLookupResult02() {
        val result = captureOutput("ExampleLookupResult02") { examples.exampleLookupResult02.main() }
        result shouldContainExactly listOf(
            "Defined(location=#/phones[0], value=JsString(123))"
        )
    }
}
