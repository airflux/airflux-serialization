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

@file:Suppress("TooManyFunctions")

package io.github.airflux.serialization.dsl.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

internal fun <L> L.left(): Either<L, Nothing> = Either.Left(this)
internal fun <R> R.right(): Either<Nothing, R> = Either.Right(this)

internal sealed interface Either<out L, out R> {

    class Left<out L>(val get: L) : Either<L, Nothing>
    class Right<out R>(val get: R) : Either<Nothing, R>

    companion object {
        val asNull: Right<Nothing?> = Right(null)
        val asTrue: Right<Boolean> = Right(true)
        val asFalse: Right<Boolean> = Right(false)
        val asUnit: Right<Unit> = Right(Unit)
        val asEmptyList: Right<List<Nothing>> = Right(emptyList())
        fun of(value: Boolean): Right<Boolean> = if (value) asTrue else asFalse
    }
}

@OptIn(ExperimentalContracts::class)
internal fun <L, R> Either<L, R>.isRight(): Boolean {
    contract {
        returns(true) implies (this@isRight is Either.Right<R>)
        returns(false) implies (this@isRight is Either.Left<L>)
    }
    return this is Either.Right<R>
}

@OptIn(ExperimentalContracts::class)
internal inline fun <L, R> Either<L, R>.isRight(predicate: (R) -> Boolean): Boolean {
    contract {
        returns(true) implies (this@isRight is Either.Right<R>)
        returns(false) implies (this@isRight is Either.Left<L>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Either.Right<R> && predicate(this.get)
}

@OptIn(ExperimentalContracts::class)
internal fun <L, R> Either<L, R>.isLeft(): Boolean {
    contract {
        returns(false) implies (this@isLeft is Either.Right<R>)
        returns(true) implies (this@isLeft is Either.Left<L>)
    }
    return this is Either.Left<L>
}

@OptIn(ExperimentalContracts::class)
internal inline fun <L, R> Either<L, R>.isLeft(predicate: (L) -> Boolean): Boolean {
    contract {
        returns(false) implies (this@isLeft is Either.Right<R>)
        returns(true) implies (this@isLeft is Either.Left<L>)
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Either.Left<L> && predicate(this.get)
}

@OptIn(ExperimentalContracts::class)
internal inline fun <L, R, U> Either<L, R>.fold(onLeft: (L) -> U, onRight: (R) -> U): U {
    contract {
        callsInPlace(onLeft, AT_MOST_ONCE)
        callsInPlace(onRight, AT_MOST_ONCE)
    }

    return if (isRight()) onRight(get) else onLeft(get)
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R, R2> Either<L, R>.map(transform: (R) -> R2): Either<L, R2> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).right() }
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R, R2> Either<L, R>.flatMap(transform: (R) -> Either<L, R2>): Either<L, R2> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isRight()) transform(this.get) else this
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R, R2> Either<L, R>.andThen(block: (R) -> Either<L, R2>): Either<L, R2> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isRight()) block(this.get) else this
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R, L2> Either<L, R>.mapLeft(transform: (L) -> L2): Either<L2, R> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMapLeft { value -> transform(value).left() }
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R, L2> Either<L, R>.flatMapLeft(transform: (L) -> Either<L2, R>): Either<L2, R> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isLeft()) transform(this.get) else this
}

@OptIn(ExperimentalContracts::class)
internal inline fun <L, R> Either<L, R>.onRight(block: (R) -> Unit): Either<L, R> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isRight()) block(it.get) }
}

@OptIn(ExperimentalContracts::class)
internal inline fun <L, R> Either<L, R>.onLeft(block: (L) -> Unit): Either<L, R> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isLeft()) block(it.get) }
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R> Either<L, R>.recover(block: (L) -> R): Either<L, R> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isRight()) this else block(this.get).right()
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R> Either<L, R>.recoverWith(block: (L) -> Either<L, R>): Either<L, R> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isRight()) this else block(this.get)
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R> Either<L, R>.getOrForward(block: (Either.Left<L>) -> Nothing): R {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isRight()) get else block(this)
}

@OptIn(ExperimentalContracts::class)
internal fun <L, R> Either<L, R>.getOrNull(): R? {
    contract {
        returns(null) implies (this@getOrNull is Either.Left<L>)
        returnsNotNull() implies ((this@getOrNull is Either.Right<R>))
    }

    return if (isRight()) get else null
}

internal infix fun <L, R> Either<L, R>.getOrElse(default: R): R = if (isRight()) get else default

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R> Either<L, R>.getOrElse(default: (L) -> R): R {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isRight()) get else default(this.get)
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R> Either<L, R>.orElse(default: () -> Either<L, R>): Either<L, R> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isRight()) this else default()
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R> Either<L, R>.orThrow(exceptionBuilder: (L) -> Throwable): R {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isRight()) get else throw exceptionBuilder(this.get)
}

@OptIn(ExperimentalContracts::class)
internal inline infix fun <L, R> Either<L, R>.forEach(block: (R) -> Unit) {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isRight()) block(this.get) else Unit
}

internal fun <T> Either<T, T>.merge(): T = if (isRight()) this.get else this.get

internal fun <L, R> Iterable<Either<L, R>>.sequence(): Either<L, List<R>> {
    val items = buildList {
        val iter = this@sequence.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            if (item.isRight()) add(item.get) else return item
        }
    }
    return if (items.isNotEmpty()) items.right() else Either.asEmptyList
}

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
internal inline fun <T, L, R> Iterable<T>.traverse(transform: (T) -> Either<L, R>): Either<L, List<R>> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    val items = buildList {
        val iter = this@traverse.iterator()
        while (iter.hasNext()) {
            val item = transform(iter.next())
            if (item.isRight()) add(item.get) else return item
        }
    }
    return if (items.isNotEmpty()) items.right() else Either.asEmptyList
}
