/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.serialization.common.kotest

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/*
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
                x.equals(null) shouldBe false
            }
            withClue("Any") {
                x.equals(Any()) shouldBe false
            }
            withClue("other") {
                x.equals(other) shouldBe false
            }
        }
    }
}
*/

internal fun <T : Any> T.shouldBeEqualsContract(y: T, z: T, other: T) =
    this.shouldBeEqualsContract(y, z, listOf(other))

@Suppress("ReplaceCallWithBinaryOperator", "EqualsNullCall")
internal fun <T : Any> T.shouldBeEqualsContract(y: T, z: T, others: Collection<T>) {
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
                x.equals(null) shouldBe false
            }
            withClue("Any") {
                x.equals(Any()) shouldBe false
            }
            withClue("other") {
                others.forEach { other ->
                    x.equals(other) shouldBe false
                }
            }
        }
    }
}
