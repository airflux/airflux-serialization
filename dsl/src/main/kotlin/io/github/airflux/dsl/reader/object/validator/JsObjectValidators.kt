package io.github.airflux.dsl.reader.`object`.validator

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty

typealias BeforeValidatorInitializer =
        (config: ObjectReaderConfiguration, properties: List<JsReaderProperty>) -> JsObjectValidator.Before?

typealias AfterValidatorInitializer =
        (config: ObjectReaderConfiguration, properties: List<JsReaderProperty>) -> JsObjectValidator.After?

class JsObjectValidators private constructor(
    val before: JsObjectValidator.Before?,
    val after: JsObjectValidator.After?
) {

    class Builder {
        var before: BeforeValidatorInitializer? = null
        var after: AfterValidatorInitializer? = null

        fun before(block: BeforeValidatorInitializer?) {
            before = block
        }

        fun after(block: AfterValidatorInitializer?) {
            after = block
        }

        fun build(config: ObjectReaderConfiguration, properties: List<JsReaderProperty>): JsObjectValidators =
            JsObjectValidators(
                before = before?.invoke(config, properties),
                after = after?.invoke(config, properties)
            )
    }
}
