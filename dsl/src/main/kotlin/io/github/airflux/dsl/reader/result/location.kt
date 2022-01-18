package io.github.airflux.dsl.reader.result

import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.result.JsLocation

operator fun JsLocation.div(key: String): JsLocation = append(PathElement.Key(key))
operator fun JsLocation.div(idx: Int): JsLocation = append(PathElement.Idx(idx))
operator fun JsLocation.div(element: PathElement): JsLocation = append(element)
