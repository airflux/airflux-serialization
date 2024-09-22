// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.test

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.knit.test.captureOutput

internal class JsLookup01Test : AnnotationSpec() {
    @Test
    fun testExampleLookup01() {
        val result = captureOutput("ExampleLookup01") { examples.exampleLookup01.main() }
        result shouldContainExactly listOf(
            "Defined(location=#/id, value=JsString(123))"
        )
    }
}
