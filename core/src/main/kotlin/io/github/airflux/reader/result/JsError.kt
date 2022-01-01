package io.github.airflux.reader.result

interface JsError {

    val level: Level
        get() = Level.NORMAL

    enum class Level {
        NORMAL, CRITICAL
    }
}
