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

package io.github.airflux.serialization.dsl.reader.struct.builder.property.specification

import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.or
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.Validator

public sealed class StructPropertySpec<EB, CTX, T> {
    public abstract val path: PropertyPaths
    public abstract val reader: Reader<EB, CTX, T>

    public class NonNullable<EB, CTX, T : Any> internal constructor(
        override val path: PropertyPaths,
        override val reader: Reader<EB, CTX, T>
    ) : StructPropertySpec<EB, CTX, T>() {

        public infix fun validation(validator: Validator<EB, CTX, T>): NonNullable<EB, CTX, T> =
            NonNullable(
                path = path,
                reader = { env, location, source ->
                    reader.read(env, location, source).validation(env, validator)
                }
            )

        public infix fun or(alt: NonNullable<EB, CTX, T>): NonNullable<EB, CTX, T> =
            NonNullable(path = path.append(alt.path), reader = reader or alt.reader)
    }

    public class Nullable<EB, CTX, T : Any> internal constructor(
        override val path: PropertyPaths,
        override val reader: Reader<EB, CTX, T?>
    ) : StructPropertySpec<EB, CTX, T?>() {

        public infix fun validation(validator: Validator<EB, CTX, T?>): Nullable<EB, CTX, T> =
            Nullable(
                path = path,
                reader = { env, location, source ->
                    reader.read(env, location, source).validation(env, validator)
                }
            )

        public infix fun filter(predicate: ReaderPredicate<EB, CTX, T>): Nullable<EB, CTX, T> =
            Nullable(
                path = path,
                reader = { env, location, source ->
                    reader.read(env, location, source).filter(env, predicate)
                }
            )

        public infix fun or(alt: Nullable<EB, CTX, T>): Nullable<EB, CTX, T> =
            Nullable(path = path.append(alt.path), reader = reader or alt.reader)
    }
}
