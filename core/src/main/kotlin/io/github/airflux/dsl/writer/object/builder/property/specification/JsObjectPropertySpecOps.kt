package io.github.airflux.dsl.writer.`object`.builder.property.specification

import io.github.airflux.core.writer.JsWriter

public fun <T : Any, P : Any> required(
    name: String,
    from: (T) -> P,
    writer: JsWriter<P>
): JsObjectPropertySpec.Required<T, P> = JsObjectRequiredPropertySpec(name, from, writer)

public fun <T : Any, P : Any> optional(
    name: String,
    from: (T) -> P?,
    writer: JsWriter<P>
): JsObjectPropertySpec.Optional<T, P> = JsObjectOptionalPropertySpec.of(name, from, writer)

public fun <T : Any, P : Any> nullable(
    name: String,
    from: (T) -> P?,
    writer: JsWriter<P>
): JsObjectPropertySpec.Nullable<T, P> = JsObjectNullablePropertySpec.of(name, from, writer)
