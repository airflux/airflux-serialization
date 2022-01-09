package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator.After
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator.Before
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsErrors
import io.github.airflux.value.JsObject

@Suppress("unused")
sealed interface JsObjectValidator {

    fun interface Before : JsObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            context: JsReaderContext,
            properties: List<JsReaderProperty>,
            input: JsObject
        ): JsErrors?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        infix fun or(other: Before): Before {
            val self = this
            return Before { configuration, context, properties, input ->
                self.validation(configuration, context, properties, input)
                    ?.let { error ->
                        other.validation(configuration, context, properties, input)
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
        infix fun and(other: Before): Before {
            val self = this
            return Before { configuration, context, properties, input ->
                when (val result = self.validation(configuration, context, properties, input)) {
                    null -> other.validation(configuration, context, properties, input)
                    else -> result
                }
            }
        }
    }

    fun interface After : JsObjectValidator {

        fun validation(
            configuration: ObjectReaderConfiguration,
            context: JsReaderContext,
            properties: List<JsReaderProperty>,
            objectValuesMap: ObjectValuesMap,
            input: JsObject
        ): JsErrors?

        /*
        * | This | Other  | Result |
        * |------|--------|--------|
        * | S    | ignore | S      |
        * | F    | S      | S      |
        * | F    | F`     | F + F` |
        */
        infix fun or(other: After): After {
            val self = this
            return After { configuration, context, properties, objectValuesMap, input ->
                self.validation(configuration, context, properties, objectValuesMap, input)
                    ?.let { error ->
                        other.validation(configuration, context, properties, objectValuesMap, input)
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
        infix fun and(other: After): After {
            val self = this
            return After { configuration, context, properties, objectValuesMap, input ->
                when (val result = self.validation(configuration, context, properties, objectValuesMap, input)) {
                    null -> other.validation(configuration, context, properties, objectValuesMap, input)
                    else -> result
                }
            }
        }
    }
}
