package io.github.airflux.dsl.writer.`object`

import io.github.airflux.dsl.AirfluxMarker

class ObjectWriterConfiguration private constructor(
    val skipPropertyIfArrayIsEmpty: Boolean,
    val skipPropertyIfObjectIsEmpty: Boolean,
    val writeNullIfArrayIsEmpty: Boolean,
    val writeNullIfObjectIsEmpty: Boolean
) {

    @AirfluxMarker
    class Builder(base: ObjectWriterConfiguration) {

        var skipPropertyIfArrayIsEmpty = base.skipPropertyIfArrayIsEmpty
        var skipPropertyIfObjectIsEmpty = base.skipPropertyIfObjectIsEmpty
        var writeNullIfArrayIsEmpty = base.writeNullIfArrayIsEmpty
        var writeNullIfObjectIsEmpty = base.writeNullIfObjectIsEmpty

        fun build(): ObjectWriterConfiguration = ObjectWriterConfiguration(
            skipPropertyIfArrayIsEmpty = skipPropertyIfArrayIsEmpty,
            skipPropertyIfObjectIsEmpty = skipPropertyIfObjectIsEmpty,
            writeNullIfArrayIsEmpty = writeNullIfArrayIsEmpty,
            writeNullIfObjectIsEmpty = writeNullIfObjectIsEmpty
        )
    }

    companion object {
        val Default = ObjectWriterConfiguration(
            skipPropertyIfArrayIsEmpty = false,
            skipPropertyIfObjectIsEmpty = false,
            writeNullIfArrayIsEmpty = false,
            writeNullIfObjectIsEmpty = false
        )

        fun build(block: Builder.() -> Unit): ObjectWriterConfiguration = Builder(Default).apply(block).build()
    }
}
