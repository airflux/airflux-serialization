/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.dsl.extension

import io.github.airflux.core.path.JsPath

infix operator fun JsPath.Companion.div(key: String): JsPath = JsPath(key)
infix operator fun JsPath.Companion.div(idx: Int): JsPath = JsPath(idx)

infix operator fun JsPath.div(key: String): JsPath = append(key)
infix operator fun JsPath.div(idx: Int): JsPath = append(idx)
