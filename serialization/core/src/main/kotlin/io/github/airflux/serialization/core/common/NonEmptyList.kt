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

package io.github.airflux.serialization.core.common

@JvmInline
public value class NonEmptyList<out T> @PublishedApi internal constructor(@PublishedApi internal val items: List<T>) {

    public fun toList(): List<T> = items

    public operator fun iterator(): Iterator<T> = items.iterator()

    public companion object {

        @JvmStatic
        public operator fun <T> invoke(head: T, vararg tail: T): NonEmptyList<T> = invoke(head, tail.toList())

        @JvmStatic
        public operator fun <T> invoke(head: T, tail: List<T>): NonEmptyList<T> = NonEmptyList(
            buildList {
                add(head)
                addAll(tail)
            }
        )

        @JvmStatic
        public fun <T> valueOf(list: List<T>): NonEmptyList<T>? =
            list.takeIf { it.isNotEmpty() }
                ?.let { NonEmptyList(it) }

        @JvmStatic
        public fun <T> add(list: NonEmptyList<T>, item: T): NonEmptyList<T> =
            NonEmptyList(list.items + item)

        @JvmStatic
        public fun <T> add(list: NonEmptyList<T>, other: NonEmptyList<T>): NonEmptyList<T> =
            addAll(list, other.items)

        @JvmStatic
        public fun <T> addAll(list: NonEmptyList<T>, items: List<T>): NonEmptyList<T> =
            if (items.isEmpty()) list else NonEmptyList(list.items + items)
    }
}

public fun <T> NonEmptyList<T>.exists(predicate: (T) -> Boolean): Boolean = items.find { predicate(it) } != null

public operator fun <T> NonEmptyList<T>.plus(item: T): NonEmptyList<T> = NonEmptyList.add(this, item)

public operator fun <T> NonEmptyList<T>.plus(items: List<T>): NonEmptyList<T> = NonEmptyList.addAll(this, items)

public operator fun <T> NonEmptyList<T>.plus(other: NonEmptyList<T>): NonEmptyList<T> =
    NonEmptyList.add(this, other)
