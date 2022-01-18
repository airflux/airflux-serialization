package io.github.airflux.dsl.reader.result

import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.result.JsLocation

operator fun JsLocation.div(child: String): JsLocation = append(PathElement.Key(child))
operator fun JsLocation.div(idx: Int): JsLocation = append(PathElement.Idx(idx))
operator fun JsLocation.div(idx: PathElement): JsLocation = append(idx)
