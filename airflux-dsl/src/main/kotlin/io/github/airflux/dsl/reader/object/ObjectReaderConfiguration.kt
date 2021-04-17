package io.github.airflux.dsl.reader.`object`

class ObjectReaderConfiguration(val failFast: Boolean) {

    @ObjectReaderMarker
    class Builder(base: ObjectReaderConfiguration = Default) {

        var failFast = base.failFast

        fun build(): ObjectReaderConfiguration = ObjectReaderConfiguration(failFast = failFast)
    }

    companion object {
        val Default = ObjectReaderConfiguration(failFast = true)
    }
}
