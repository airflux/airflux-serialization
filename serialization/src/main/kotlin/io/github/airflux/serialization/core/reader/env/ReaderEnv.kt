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

package io.github.airflux.serialization.core.reader.env

import io.github.airflux.serialization.core.reader.env.exception.ExceptionsHandler

/**
 * The environment of reading.
 * @param errorBuilders the builders of error.
 * @param options the options of reading.
 * @param exceptionsHandler handler **uncaught** exceptions.
 */
public class ReaderEnv<EB, O>(
    public val errorBuilders: EB,
    public val options: O,
    public val exceptionsHandler: ExceptionsHandler<EB, O>? = null
)
