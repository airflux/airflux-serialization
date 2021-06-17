package io.github.airflux.path

import io.github.airflux.common.ObjectContract
import io.github.airflux.path.JsPath.Identifiable.Companion.div
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class JsPathTest {

    @Nested
    inner class CompanionObject {

        @Nested
        inner class OperatorDivForKey {

            @Test
            fun `Testing operator 'div' for Root`() {
                val target = JsPath.Root

                val path = target / "user"

                path as JsPath.Identifiable.Simple
                assertContentEquals(expected = listOf(KeyPathElement("user")), actual = path)
                assertEquals(expected = "#/user", actual = path.toString())
            }

            @Test
            fun `Testing operator 'div' for Simple`() {
                val target = (JsPath.Root / "user") as JsPath.Identifiable.Simple

                val path = target / "id"

                path as JsPath.Identifiable.Composite
                assertContentEquals(expected = listOf(KeyPathElement("user"), KeyPathElement("id")), actual = path)
                assertEquals(expected = "#/user/id", actual = path.toString())
            }

            @Test
            fun `Testing operator 'div' for Composite`() {
                val target = (JsPath.Root / "user" / "phones") as JsPath.Identifiable.Composite

                val path = target / "work"

                path as JsPath.Identifiable.Composite
                assertContentEquals(
                    expected = listOf(
                        KeyPathElement("user"),
                        KeyPathElement("phones"),
                        KeyPathElement("work")
                    ), actual = path
                )
                assertEquals(expected = "#/user/phones/work", actual = path.toString())
            }
        }

        @Nested
        inner class OperatorDivForIdx {

            @Test
            fun `Testing operator 'div' for Root`() {
                val target = JsPath.Root

                val path = target / 0

                path as JsPath.Identifiable.Simple
                assertContentEquals(expected = listOf(IdxPathElement(0)), actual = path)
                assertEquals(expected = "#[0]", actual = path.toString())
            }

            @Test
            fun `Testing operator 'div' for Simple`() {
                val target = (JsPath.Root / "user") as JsPath.Identifiable.Simple

                val path = target / 0

                path as JsPath.Identifiable.Composite
                assertContentEquals(expected = listOf(KeyPathElement("user"), IdxPathElement(0)), actual = path)
                assertEquals(expected = "#/user[0]", actual = path.toString())
            }

            @Test
            fun `Testing operator 'div' for Composite`() {
                val target = (JsPath.Root / "user" / "phones") as JsPath.Identifiable.Composite

                val path = target / 0

                path as JsPath.Identifiable.Composite
                assertContentEquals(
                    expected = listOf(
                        KeyPathElement("user"),
                        KeyPathElement("phones"),
                        IdxPathElement(0)
                    ), actual = path
                )
                assertEquals(expected = "#/user/phones[0]", actual = path.toString())
            }
        }
    }

    @Nested
    inner class Constructors {

        @Test
        fun `Testing the constructor of the JsLookupPath class with text parameter`() {
            val path = JsPath.Root / "user"

            path as JsPath.Identifiable.Simple
            assertContentEquals(expected = listOf(KeyPathElement("user")), actual = path)
        }

        @Test
        fun `Testing the constructor of the JsLookupPath class with number parameter`() {
            val path = JsPath.Root / 10

            path as JsPath.Identifiable.Simple
            assertContentEquals(expected = listOf(IdxPathElement(10)), actual = path)
        }
    }

    @Test
    fun `Testing 'toString' function of the JsLookupPath class`() {
        ObjectContract.checkToString(JsPath.Root, "#")
        ObjectContract.checkToString(JsPath.Root / "user", "#/user")
        ObjectContract.checkToString(JsPath.Root / "user" / "name", "#/user/name")
    }

    @Test
    fun `Testing 'equals contract' of the JsLookupPath class (Simple)`() {
        ObjectContract.checkEqualsContract(
            JsPath.Root / "name",
            JsPath.Root / "name",
            JsPath.Root / "phones"
        )
    }

    @Test
    fun `Testing 'equals contract' of the JsLookupPath class (Composite)`() {
        ObjectContract.checkEqualsContract(
            JsPath.Root / "user" / "name",
            JsPath.Root / "user" / "name",
            JsPath.Root / "user" / "phones"
        )
    }

    @Test
    fun `Create a path by two text elements`() {
        val path = "user" / "name"

        path as JsPath.Identifiable.Composite
        assertContentEquals(expected = listOf(KeyPathElement("user"), KeyPathElement("name")), actual = path)
        assertEquals(expected = "#/user/name", actual = path.toString())
    }

    @Test
    fun `Create a path by text element and index element`() {
        val path = "phones" / 0

        path as JsPath.Identifiable.Composite
        assertContentEquals(expected = listOf(KeyPathElement("phones"), IdxPathElement(0)), actual = path)
        assertEquals(expected = "#/phones[0]", actual = path.toString())
    }
}
