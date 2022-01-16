package io.github.airflux.dsl.extension

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.IdxPathElement
import io.github.airflux.core.path.KeyPathElement
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsValue

operator fun JsValue.div(name: String): JsLookup = JsLookup.apply(JsLocation.Root, KeyPathElement(name), this)
operator fun JsValue.div(idx: Int): JsLookup = JsLookup.apply(JsLocation.Root, IdxPathElement(idx), this)
