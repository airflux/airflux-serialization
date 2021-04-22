package io.github.airflux.sample.dto.reader.simple.base

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.readOptional
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.result.JsResult
import io.github.airflux.sample.dto.reader.base.ErrorBuilder
import io.github.airflux.value.JsValue

fun <T : Any> readRequired(from: JsValue, byPath: JsPath, using: JsReader<T>): JsResult<T> =
    readRequired(from, byPath, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

fun <T : Any> readRequired(from: JsValue, byName: String, using: JsReader<T>): JsResult<T> =
    readRequired(from, byName, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

fun <T : Any> readOptional(from: JsValue, byPath: JsPath, using: JsReader<T>): JsResult<T?> =
    readOptional(from, byPath, using, ErrorBuilder.InvalidType)

fun <T : Any> readOptional(from: JsValue, byName: String, using: JsReader<T>): JsResult<T?> =
    readOptional(from, byName, using, ErrorBuilder.InvalidType)
