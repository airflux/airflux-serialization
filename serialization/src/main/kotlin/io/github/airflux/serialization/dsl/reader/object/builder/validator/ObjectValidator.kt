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

package io.github.airflux.serialization.dsl.reader.`object`.builder.validator

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperties

public fun interface ObjectValidator {

    public fun validate(
        context: ReaderContext,
        location: Location,
        properties: ObjectProperties,
        input: StructNode
    ): ReaderResult.Failure?

    /*
    * | This | Other  | Result |
    * |------|--------|--------|
    * | S    | ignore | S      |
    * | F    | S      | S      |
    * | F    | F`     | F + F` |
    */
    public infix fun or(alt: ObjectValidator): ObjectValidator {
        val self = this
        return ObjectValidator { context, location, properties, input ->
            self.validate(context, location, properties, input)
                ?.let { error ->
                    alt.validate(context, location, properties, input)
                        ?.let { error + it }
                }
        }
    }

    /*
     * | This | Other  | Result |
     * |------|--------|--------|
     * | S    | S      | S      |
     * | S    | F      | F      |
     * | F    | ignore | F      |
     */
    public infix fun and(alt: ObjectValidator): ObjectValidator {
        val self = this
        return ObjectValidator { context, location, properties, input ->
            val result = self.validate(context, location, properties, input)
            result ?: alt.validate(context, location, properties, input)
        }
    }
}
