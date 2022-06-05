package io.github.airflux.dsl.reader.array

import io.github.airflux.dsl.reader.array.item.specification.JsArrayItemSpec

@Suppress("unused")
fun <T> prefixItems(
    item: JsArrayItemSpec<T>,
    vararg items: JsArrayItemSpec<T>
): JsArrayPrefixItemsSpec<T> = JsArrayPrefixItemsSpec(item = item, items = items)

class JsArrayPrefixItemsSpec<out T> private constructor(val items: List<JsArrayItemSpec<T>>) {

    internal constructor(item: JsArrayItemSpec<T>, vararg items: JsArrayItemSpec<T>) : this(
        mutableListOf<JsArrayItemSpec<T>>()
            .apply {
                this.add(item)
                this.addAll(items)
            }
    )
}
