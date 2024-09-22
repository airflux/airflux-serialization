// This file was automatically generated from JsLookupResult.kt by Knit tool. Do not edit.
package examples.test

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.knit.test.captureOutput

internal class JsLookup03Test : AnnotationSpec() {
    @Test
    fun testExampleLookup03() {
        val result = captureOutput("ExampleLookup03") { examples.exampleLookup03.main() }
        result shouldContainExactly listOf(
            "Defined(location=#/user/phones[0], value=JsString(123))"
        )
    }
}
