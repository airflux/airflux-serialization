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

package io.github.airflux.serialization.dsl.common

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.assertions.print.Printed

import io.kotest.matchers.shouldBe
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal inline fun <reified R> Either<*, R>.shouldBeRight(): Either.Right<R> {
    contract {
        returns() implies (this@shouldBeRight is Either.Right<R>)
    }

    if (this.isLeft()) {
        errorCollector.collectOrThrow(
            failure(
                expected = Either.Right::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Right, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as Either.Right<R>
}

internal inline infix fun <reified R> Either<*, R>.shouldBeRight(expected: R) {
    this.shouldBeRight().get shouldBe expected
}

internal inline infix fun <reified R> Either<*, R>.shouldBeRight(block: (R) -> Unit) {
    block(this.shouldBeRight().get)
}

@OptIn(ExperimentalContracts::class)
internal inline fun <reified L> Either<L, *>.shouldBeLeft(): Either.Left<L> {
    contract {
        returns() implies (this@shouldBeLeft is Either.Left<L>)
    }

    if (this.isRight()) {
        errorCollector.collectOrThrow(
            failure(
                expected = Either.Left::class.qualifiedName!!,
                actual = this::class.qualifiedName!!,
                failureMessage = "Expected a Left, but got ${this::class.simpleName}. "
            )
        )
    }
    return this as Either.Left<L>
}

internal inline infix fun <reified L> Either<L, *>.shouldBeLeft(expected: L) {
    this.shouldBeLeft().get shouldBe expected
}

internal inline infix fun <reified L> Either<L, *>.shouldBeLeft(block: (L) -> Unit) {
    block(this.shouldBeLeft().get)
}

internal fun failure(expected: String, actual: String, failureMessage: String): Throwable =
    failure(Expected(Printed(expected)), Actual(Printed(actual)), failureMessage)
