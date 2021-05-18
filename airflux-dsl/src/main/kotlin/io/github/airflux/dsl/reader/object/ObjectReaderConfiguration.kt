package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.AirfluxMarker

class ObjectReaderConfiguration(val failFast: Boolean) {

    @AirfluxMarker
    class Builder(base: ObjectReaderConfiguration = Default) {

        var failFast = base.failFast

        fun build(): ObjectReaderConfiguration = ObjectReaderConfiguration(failFast = failFast)
    }

    companion object {
        val Default = ObjectReaderConfiguration(failFast = true)
    }
}
