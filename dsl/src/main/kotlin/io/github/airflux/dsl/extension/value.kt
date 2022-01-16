package io.github.airflux.dsl.extension

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsValue

operator fun JsValue.div(name: String): JsLookup = JsLookup.apply(JsLocation.Root, PathElement.Key(name), this)
operator fun JsValue.div(idx: Int): JsLookup = JsLookup.apply(JsLocation.Root, PathElement.Idx(idx), this)
