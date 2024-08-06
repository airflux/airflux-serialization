/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.common

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.assertDoesNotThrow

internal class EitherTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"
    }

    init {

        "The `Either` type properties" - {

            "the `asNull` property should return the `Either#Right` type with the `null` value" {
                val value: Either<Unit, String?> = Either.asNull
                value shouldBeRight null
            }

            "the `asTrue` property should return the `Either#Right` type with the `true` value" {
                val value = Either.asTrue
                value shouldBeRight true
            }

            "the `asFalse` property should return the `Either#Right` type with the `false` value" {
                val value = Either.asFalse
                value shouldBeRight false
            }

            "the `asUnit` property should return the `Either#Right` type with the `Unit` value" {
                val value: Either<Int, Unit> = Either.asUnit
                value shouldBeRight Unit
            }

            "the `asEmptyList` property should return the `Either#Right` type with the `empty list` value" {
                val value: Either<Int, List<String>> = Either.asEmptyList
                value shouldBeRight emptyList()
            }
        }

        "The `Either` type functions" - {

            "the `of` function" - {

                "when a parameter has the `true` value" - {
                    val param = true

                    "then should return the `Either#Right` type with the `true` value" {
                        val value: Either<Int, Boolean> = Either.of(param)
                        value shouldBeRight true
                    }
                }

                "when a parameter has the `false` value" - {
                    val param = false

                    "then should return the `Either#Right` type with the `true` value" {
                        val value: Either<Int, Boolean> = Either.of(param)
                        value shouldBeRight false
                    }
                }
            }

            "the `isRight` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return the true value" {
                        val value: Boolean = original.isRight()
                        value shouldBe true
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the false value" {
                        val value: Boolean = original.isRight()
                        value shouldBe false
                    }
                }
            }

            "the `isSuccess` function with predicate" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "when the predicate return the true value" - {
                        val predicate: (String) -> Boolean = { it == ORIGINAL_VALUE }

                        "then should return the true value" {
                            val value: Boolean = original.isRight(predicate)
                            value shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (String) -> Boolean = { it != ORIGINAL_VALUE }

                        "then should return the true value" {
                            val value: Boolean = original.isRight(predicate)
                            value shouldBe false
                        }
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        val value: Boolean = original.isRight(predicate)
                        value shouldBe false
                    }
                }
            }

            "the `isFailure` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return the false value" {
                        val value: Boolean = original.isLeft()
                        value shouldBe false
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the true value" {
                        val value: Boolean = original.isLeft()
                        value shouldBe true
                    }
                }
            }

            "the `isFailure` function with predicate" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()
                    val predicate: (Error) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        val value: Boolean = original.isLeft(predicate)
                        value shouldBe false
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "when the predicate return the true value" - {
                        val predicate: (Error) -> Boolean = { it == Error.Empty }

                        "then should return the true value" {
                            val value: Boolean = original.isLeft(predicate)
                            value shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (Error) -> Boolean = { it != Error.Empty }

                        "then should return the true value" {
                            val value: Boolean = original.isLeft(predicate)
                            value shouldBe false
                        }
                    }
                }
            }

            "the `fold` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val value = original.fold(onLeft = { ALTERNATIVE_VALUE }, onRight = { it })
                        value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the null value" {
                        val value = original.fold(onLeft = { ALTERNATIVE_VALUE }, onRight = { it })
                        value shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `map` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a result of applying the [transform] function to the value" {
                        val value = original.map { it.toInt() }
                        value shouldBeRight ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return an original do not apply the [transform] function to a value" {
                        val value = original.map { it.toInt() }
                        value shouldBe original
                    }
                }
            }

            "the `flatMap` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a result of applying the [transform] function to the value" {
                        val value = original.flatMap { result -> result.toInt().right() }
                        value shouldBeRight ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return an original do not apply the [transform] function to a value" {
                        val value = original.flatMap { success ->
                            success.toInt().right()
                        }

                        value shouldBe original
                    }
                }
            }

            "the `andThen` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a result of invoke the [block]" {
                        val value = original.andThen { result -> result.toInt().right() }
                        value shouldBeRight ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return an original do not invoke the [block]" {
                        val value = original.andThen { success ->
                            success.toInt().right()
                        }

                        value shouldBe original
                    }
                }
            }

            "the `mapLeft` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return an original do not apply the [transform] function to an error" {
                        val value = original.mapLeft { Error.Blank }
                        value shouldBeRight ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return a result of applying the [transform] function to an error" {
                        val value = original.mapLeft { Error.Blank }
                        value shouldBeLeft Error.Blank
                    }
                }
            }

            "the `flatMapLeft` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return an original do not apply the [transform] function to an error" {
                        val value = original.flatMapLeft { Error.Blank.left() }
                        value shouldBeRight ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return a result of applying the [transform] function to an error" {
                        val value = original.flatMapLeft { Error.Blank.left() }
                        value shouldBeLeft Error.Blank
                    }
                }
            }

            "the `onRight` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onRight { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should not anything do" {
                        assertDoesNotThrow {
                            original.onRight { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onLeft` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should not anything do" {
                        assertDoesNotThrow {
                            original.onLeft { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onLeft { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getOrForward` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val value = original.getOrForward { throw IllegalStateException() }
                        value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.getOrForward { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `recover` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return an original value" {
                        val value = original.recover { ALTERNATIVE_VALUE }
                        value shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the result of invoking the recovery function" {
                        val value = original.recover { ALTERNATIVE_VALUE }
                        value shouldBeRight ALTERNATIVE_VALUE
                    }
                }
            }

            "the `recoverWith` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return an original value" {
                        val value = original.recoverWith { ALTERNATIVE_VALUE.right() }
                        value shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the result of invoking the recovery function" {
                        val value = original.recoverWith { ALTERNATIVE_VALUE.right() }
                        value shouldBeRight ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrNull` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val value = original.getOrNull()
                        value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the null value" {
                        val value = original.getOrNull()
                        value.shouldBeNull()
                    }
                }
            }

            "the `getOrElse` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val value = original.getOrElse(ALTERNATIVE_VALUE)
                        value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the defaultValue value" {
                        val value = original.getOrElse(ALTERNATIVE_VALUE)
                        value shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrElse` function with a predicate" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val value = original.getOrElse { ALTERNATIVE_VALUE }
                        value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return a value from a handler" {
                        val value = original.getOrElse { ALTERNATIVE_VALUE }
                        value shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `orElse` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val elseResult: Either<Error, String> = ALTERNATIVE_VALUE.right()
                        val value = original.orElse { elseResult }
                        value shouldBe original
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return the defaultValue value" {
                        val elseResult: Either<Error, String> = ALTERNATIVE_VALUE.right()
                        val value = original.orElse { elseResult }
                        value shouldBe elseResult
                    }
                }
            }

            "the `orThrow` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val value = original.orThrow { throw IllegalStateException() }
                        value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.orThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `forEach` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<Error, String> = ORIGINAL_VALUE.right()

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<Error, String> = Error.Empty.left()

                    "then should not thrown exception" {
                        shouldNotThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `merge` function" - {

                "when a variable has the `Either#Right` type" - {
                    val original: Either<String, String> = ORIGINAL_VALUE.right()

                    "then should return a value" {
                        val value = original.merge()
                        value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Either#Left` type" - {
                    val original: Either<String, String> = ALTERNATIVE_VALUE.left()

                    "then should return the error value" {
                        val value = original.merge()
                        value shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `sequence` function" - {

                "when a collection is empty" - {
                    val original: List<Either<Error, String>> = listOf()

                    "then should return the value of the asEmptyList property" {
                        val value = original.sequence()
                        value.shouldBeRight()
                        value shouldBeSameInstanceAs Either.asEmptyList
                    }
                }

                "when a collection has items only the `Either#Right` type" - {
                    val original: List<Either<Error, String>> =
                        listOf(ORIGINAL_VALUE.right(), ALTERNATIVE_VALUE.right())

                    "then should return a list with all values" {
                        val value: Either<Error, List<String>> = original.sequence()
                        value.shouldBeRight()
                        value.get shouldContainExactly listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    }
                }

                "when a collection has a item of the `Either#Left` type" - {
                    val original: List<Either<Error, String>> = listOf(ORIGINAL_VALUE.right(), Error.Empty.left())

                    "then should return the error value" {
                        val value = original.sequence()
                        value shouldBeLeft Error.Empty
                    }
                }
            }

            "the `traverse` function" - {

                "when a collection is empty" - {
                    val original: List<String> = listOf()
                    val transform: (String) -> Either<Error, Int> = { it.toInt().right() }

                    "then should return the value of the asEmptyList property" {
                        val value: Either<Error, List<Int>> = original.traverse(transform)
                        value.shouldBeRight()
                        value shouldBeSameInstanceAs Either.asEmptyList
                    }
                }

                "when a transform function returns items only the `Either#Right` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> Either<Error, Int> = { it.toInt().right() }

                    "then should return a list with all transformed values" {
                        val value: Either<Error, List<Int>> = original.traverse(transform)
                        value.shouldBeRight()
                        value.get shouldContainExactly listOf(ORIGINAL_VALUE.toInt(), ALTERNATIVE_VALUE.toInt())
                    }
                }

                "when a transform function returns any item of the `Either#Left` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> Either<Error, Int> = {
                        val res = it.toInt()
                        if (res > 10) Error.Empty.left() else res.right()
                    }

                    "then should return the error value" {
                        val value: Either<Error, List<Int>> = original.traverse(transform)
                        value shouldBeLeft Error.Empty
                    }
                }
            }
        }

        "The `success` function should return the `Either#Right` type with the passed value" {
            val value: Either<Error.Empty, String> = ORIGINAL_VALUE.right()
            value shouldBeRight ORIGINAL_VALUE
        }

        "The `error` function should return the `Either#Left` type with the passed value" {
            val value: Either<Error.Empty, String> = Error.Empty.left()
            value shouldBeLeft Error.Empty
        }
    }

    internal sealed interface Error {
        object Empty : Error
        object Blank : Error
    }
}
