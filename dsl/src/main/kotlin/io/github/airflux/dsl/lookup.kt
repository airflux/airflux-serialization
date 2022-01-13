package io.github.airflux.dsl

import io.github.airflux.lookup.JsLookup

operator fun JsLookup.div(key: String): JsLookup = apply(key)
operator fun JsLookup.div(idx: Int): JsLookup = apply(idx)
