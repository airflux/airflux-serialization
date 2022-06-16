package io.github.airflux.dsl.reader.array.builder.item.specification

@Suppress("unused")
public fun <T> prefixItems(
    item: JsArrayItemSpec<T>,
    vararg items: JsArrayItemSpec<T>
): JsArrayPrefixItemsSpec<T> = JsArrayPrefixItemsSpec(item = item, items = items)

public class JsArrayPrefixItemsSpec<T> private constructor(public val items: List<JsArrayItemSpec<T>>) {

    internal constructor(item: JsArrayItemSpec<T>, vararg items: JsArrayItemSpec<T>) : this(
        mutableListOf<JsArrayItemSpec<T>>()
            .apply {
                this.add(item)
                this.addAll(items)
            }
    )
}
