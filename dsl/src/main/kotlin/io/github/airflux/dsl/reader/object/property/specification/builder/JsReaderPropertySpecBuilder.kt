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

package io.github.airflux.dsl.reader.`object`.property.specification.builder

import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.dsl.reader.`object`.property.specification.JsReaderPropertySpec
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder.Defaultable
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder.Nullable
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder.NullableWithDefault
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder.Optional
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder.OptionalWithDefault
import io.github.airflux.dsl.reader.`object`.property.specification.builder.JsReaderPropertySpecBuilder.Required

sealed interface JsReaderPropertySpecBuilder<T : Any> {

    fun interface Required<T : Any> : JsReaderPropertySpecBuilder<T> {

        infix fun validation(validator: JsValidator<T>): Required<T> {
            val self = this
            return Required {
                self.build().validation(validator)
            }
        }

        infix fun or(alt: Required<T>): Required<T> {
            val self = this
            return Required {
                self.build().or(alt.build())
            }
        }

        fun build(): JsReaderPropertySpec.Required<T>
    }

    fun interface Defaultable<T : Any> : JsReaderPropertySpecBuilder<T> {

        infix fun validation(validator: JsValidator<T>): Defaultable<T> {
            val self = this
            return Defaultable { invalidTypeErrorBuilder ->
                self.build(invalidTypeErrorBuilder).validation(validator)
            }
        }

        infix fun or(alt: Defaultable<T>): Defaultable<T> {
            val self = this
            return Defaultable { invalidTypeErrorBuilder ->
                self.build(invalidTypeErrorBuilder) or alt.build(invalidTypeErrorBuilder)
            }
        }

        fun build(
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder
        ): JsReaderPropertySpec.Defaultable<T>
    }

    fun interface Optional<T : Any> : JsReaderPropertySpecBuilder<T> {

        infix fun validation(validator: JsValidator<T?>): Optional<T> {
            val self = this
            return Optional {
                self.build().validation(validator)
            }
        }

        infix fun filter(predicate: JsPredicate<T>): Optional<T> {
            val self = this
            return Optional {
                self.build().filter(predicate)
            }
        }

        infix fun or(alt: Optional<T>): Optional<T> {
            val self = this
            return Optional {
                self.build() or alt.build()
            }
        }

        fun build(): JsReaderPropertySpec.Optional<T>
    }

    fun interface OptionalWithDefault<T : Any> : JsReaderPropertySpecBuilder<T> {

        infix fun validation(validator: JsValidator<T>): OptionalWithDefault<T> {
            val self = this
            return OptionalWithDefault {
                self.build().validation(validator)
            }
        }

        infix fun or(alt: OptionalWithDefault<T>): OptionalWithDefault<T> {
            val self = this
            return OptionalWithDefault {
                self.build() or alt.build()
            }
        }

        fun build(): JsReaderPropertySpec.OptionalWithDefault<T>
    }

    fun interface Nullable<T : Any> : JsReaderPropertySpecBuilder<T> {

        infix fun validation(validator: JsValidator<T?>): Nullable<T> {
            val self = this
            return Nullable {
                self.build().validation(validator)
            }
        }

        infix fun filter(predicate: JsPredicate<T>): Nullable<T> {
            val self = this
            return Nullable {
                self.build().filter(predicate)
            }
        }

        infix fun or(alt: Nullable<T>): Nullable<T> {
            val self = this
            return Nullable {
                self.build().or(alt.build())
            }
        }

        fun build(): JsReaderPropertySpec.Nullable<T>
    }

    fun interface NullableWithDefault<T : Any> : JsReaderPropertySpecBuilder<T> {

        infix fun validation(validator: JsValidator<T?>): NullableWithDefault<T> {
            val self = this
            return NullableWithDefault {
                self.build().validation(validator)
            }
        }

        infix fun filter(predicate: JsPredicate<T>): NullableWithDefault<T> {
            val self = this
            return NullableWithDefault {
                self.build().filter(predicate)
            }
        }

        infix fun or(alt: NullableWithDefault<T>): NullableWithDefault<T> {
            val self = this
            return NullableWithDefault {
                self.build()
                    .or(alt.build())
            }
        }

        fun build(): JsReaderPropertySpec.NullableWithDefault<T>
    }
}
