package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.AirfluxMarker

class ObjectReaderConfiguration private constructor(val failFast: Boolean) {

    @AirfluxMarker
    class Builder(base: ObjectReaderConfiguration) {

        var failFast = base.failFast

        fun build(): ObjectReaderConfiguration = ObjectReaderConfiguration(failFast = failFast)
    }

    companion object {
        val Default = ObjectReaderConfiguration(failFast = true)

        fun build(block: Builder.() -> Unit): ObjectReaderConfiguration = Builder(Default).apply(block).build()
    }
}
