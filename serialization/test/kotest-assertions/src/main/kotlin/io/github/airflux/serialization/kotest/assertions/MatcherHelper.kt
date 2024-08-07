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

package io.github.airflux.serialization.kotest.assertions

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.Printed

@PublishedApi
internal fun failure(expected: String, actual: String, failureMessage: String): Throwable =
    failure(Expected(Printed(expected)), Actual(Printed(actual)), failureMessage)

@PublishedApi
internal fun String.makeDescription(): String = escape()
    .takeIf { it.isNotBlank() }
    ?.let { " ($it)." }
    ?: "."

@PublishedApi
internal fun String.escape(): String = this.replace(System.lineSeparator(), ". ")
