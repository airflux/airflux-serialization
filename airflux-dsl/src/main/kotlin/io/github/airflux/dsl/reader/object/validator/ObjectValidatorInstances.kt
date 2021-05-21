package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.reader.`object`.validator.ObjectValidator

internal class ObjectValidatorInstances(
    val before: List<ObjectValidator.Before>,
    val after: List<ObjectValidator.After>
)
