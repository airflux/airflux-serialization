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
