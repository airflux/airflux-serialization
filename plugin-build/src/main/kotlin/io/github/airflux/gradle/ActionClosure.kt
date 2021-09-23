package io.github.airflux.gradle

import org.gradle.api.Action

interface ActionClosure<T> : Action<T> {
    companion object {
        operator fun <T> invoke(block: T.() -> Unit): ActionClosure<T> =
            object : ActionClosure<T> {
                override fun execute(receiver: T) {
                    block(receiver)
                }
            }
    }
}
