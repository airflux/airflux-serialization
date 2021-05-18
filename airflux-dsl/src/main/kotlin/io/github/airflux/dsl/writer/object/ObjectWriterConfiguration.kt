package io.github.airflux.dsl.writer.`object`

class ObjectWriterConfiguration private constructor(
    val properties: Set<WriteProperty> = emptySet()
) {

    constructor(vararg properties: WriteProperty) : this(properties.toSet())

    enum class WriteProperty {
        OUTPUT_NULL_IF_ARRAY_IS_EMPTY,
        OUTPUT_NULL_IF_OBJECT_IS_EMPTY,
        SKIP_PROPERTY_IF_ARRAY_IS_EMPTY,
        SKIP_PROPERTY_IF_OBJECT_IS_EMPTY,
    }
}
