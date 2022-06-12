package io.github.airflux.common.kotest

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("ReplaceCallWithBinaryOperator", "EqualsNullCall")
internal fun <T : Any> T.shouldBeEqualsContract(y: T, z: T, other: T) {
    val x = this
    assertSoftly {
        withClue("reflexive") {
            withClue("equals") {
                x.equals(x) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe x.hashCode()
            }
        }

        withClue("symmetric") {
            withClue("equals") {
                x.equals(y) shouldBe true
                y.equals(x) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe y.hashCode()
                y.hashCode() shouldBe x.hashCode()
            }
        }

        withClue("transitive") {
            withClue("equals") {
                x.equals(y) shouldBe true
                y.equals(z) shouldBe true
                x.equals(z) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe y.hashCode()
                y.hashCode() shouldBe z.hashCode()
                x.hashCode() shouldBe z.hashCode()
            }
        }

        withClue("never equal to") {
            withClue("null") {
                x.equals(null) shouldNotBe true
            }
            withClue("Any") {
                x.equals(Any()) shouldNotBe true
            }
            withClue("other") {
                x.equals(other) shouldNotBe true
            }
        }
    }
}
