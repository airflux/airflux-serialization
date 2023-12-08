/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.test.dummy

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.predicate.JsPredicate

public class DummyReaderPredicate<EB, O, T : Any>(
    public val block: (env: JsReaderEnv<EB, O>, location: JsLocation, value: T) -> Boolean
) : JsPredicate<EB, O, T> {

    public constructor(result: Boolean) : this({ _, _, _ -> result })

    override fun test(env: JsReaderEnv<EB, O>, location: JsLocation, value: T): Boolean =
        block(env, location, value)
}
