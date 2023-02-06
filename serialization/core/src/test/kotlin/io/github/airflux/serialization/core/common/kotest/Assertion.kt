/*
 * Copyright 2021-2023 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.serialization.core.common.kotest

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

internal infix fun <T : Any> T.shouldBeEqualsString(expected: String) {
    this.toString() shouldBe expected
}

internal fun <T : Any> T.shouldBeEqualsContract(y: T, z: T, other: T) =
    this.shouldBeEqualsContract(y, z, listOf(other))

@Suppress("ReplaceCallWithBinaryOperator", "EqualsNullCall")
internal fun <T : Any> T.shouldBeEqualsContract(y: T, z: T, others: Collection<T>) {
    val x = this
    assertSoftly {
        withClue("reflexive") {
            withClue("$x equal to $x") {
                x.equals(x) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe x.hashCode()
            }
        }

        withClue("symmetric") {
            withClue("$x equal to $y") {
                x.equals(y) shouldBe true
            }
            withClue("$y equal to $x") {
                y.equals(x) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe y.hashCode()
                y.hashCode() shouldBe x.hashCode()
            }
        }

        withClue("transitive") {
            withClue("$x equal to $y") {
                x.equals(y) shouldBe true
            }
            withClue("$y equal to $z") {
                y.equals(z) shouldBe true
            }
            withClue("$x equal to $z") {
                x.equals(z) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe y.hashCode()
                y.hashCode() shouldBe z.hashCode()
                x.hashCode() shouldBe z.hashCode()
            }
        }

        withClue("$x never equal to null") {
            x.equals(null) shouldBe false
        }

        withClue("$x never equal to Any") {
            x.equals(Any()) shouldBe false
        }

        others.forEach { other ->
            withClue("$x never equal to $other") {
                x.equals(other) shouldBe false
            }
            withClue("$other never equal to $x") {
                other.equals(x) shouldBe false
            }
        }
    }
}
