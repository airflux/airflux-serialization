/*
 * Copyright 2021-2024 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.serialization.dsl.location

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath

public operator fun JsLocation.div(key: String): JsLocation = append(JsPath.Element.Key(key))
public operator fun JsLocation.div(idx: Int): JsLocation = append(JsPath.Element.Idx(idx))
public operator fun JsLocation.div(element: JsPath.Element): JsLocation = append(element)
