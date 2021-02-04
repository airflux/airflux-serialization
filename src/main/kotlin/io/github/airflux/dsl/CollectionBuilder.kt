package io.github.airflux.dsl

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
