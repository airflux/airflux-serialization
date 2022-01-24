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

package io.github.airflux.core.reader

@Suppress("unused")
fun interface CollectionBuilderFactory<in Elem, out To> {

    fun newBuilder(capacity: Int): CollectionBuilder<Elem, To>

    companion object {
        fun <T> listFactory(): CollectionBuilderFactory<T, List<T>> =
            CollectionBuilderFactory { capacity -> ListBuilder(capacity) }

        fun <T> setFactory(): CollectionBuilderFactory<T, Set<T>> =
            CollectionBuilderFactory { capacity -> SetBuilder(capacity) }
    }
}

interface CollectionBuilder<in Elem, out To> {
    operator fun plusAssign(elem: Elem)

    fun result(): To
}

private class ListBuilder<T>(capacity: Int) : CollectionBuilder<T, List<T>> {
    private val value: MutableList<T> = ArrayList(capacity)

    override fun plusAssign(elem: T) {
        value.add(elem)
    }

    override fun result(): List<T> = value
}

private class SetBuilder<T>(capacity: Int) : CollectionBuilder<T, Set<T>> {
    private val value: MutableSet<T> = LinkedHashSet(capacity)

    override fun plusAssign(elem: T) {
        value.add(elem)
    }

    override fun result(): Set<T> = value
}
