// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.test

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.knit.test.captureOutput

internal class JsLookup02Test : AnnotationSpec() {
    @Test
    fun testExampleLookup02() {
        val result = captureOutput("ExampleLookup02") { examples.exampleLookup02.main() }
        result shouldContainExactly listOf(
            "Defined(location=#[1], value=JsString(2))"
        )
    }
}
