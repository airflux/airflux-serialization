package io.github.airflux.dsl.writer.`object`.property

sealed interface JsWriterProperty<T, P : Any> {

    val name: String

    sealed interface Required<T, P : Any> : JsWriterProperty<T, P>

    sealed interface Optional<T, P : Any> : JsWriterProperty<T, P> {

        sealed interface Simple<T, P : Any> : Optional<T, P>

        sealed interface Array<T, P : Any> : Optional<T, P> {
            fun skipIfEmpty()
        }

        sealed interface Object<T, P : Any> : Optional<T, P> {
            fun skipIfEmpty()
        }
    }

    sealed interface Nullable<T, P : Any> : JsWriterProperty<T, P> {

        sealed interface Simple<T, P : Any> : Nullable<T, P>

        sealed interface Array<T, P : Any> : Nullable<T, P> {
            fun nullIfEmpty()
        }

        sealed interface Object<T, P : Any> : Nullable<T, P> {
            fun nullIfEmpty()
        }
    }
}
