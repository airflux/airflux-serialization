package io.github.airflux.core.reader.result

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.kotest.shouldBeEqualsContract
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class JsResultTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ELSE_VALUE = "20"
        private val LOCATION = JsLocation.empty.append("id")
    }

    init {

        "A JsResult#Success type" - {
            val original: JsResult<String> = JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

            "calling map function should return a result of applying the [transform] function to the value" {
                val result = original.map { it.toInt() }

                result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
            }

            "calling flatMap function should return a result of applying the [transform] function to the value" {
                val result = original.flatMap { location, value -> JsResult.Success(location, value.toInt()) }

                result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
            }

            "calling recovery function should return an original" {
                val result = original.recovery { _ -> JsResult.Success(LOCATION, ELSE_VALUE) }

                result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
            }

            "calling getOrElse function should return a value" {
                val result = original.getOrElse(ELSE_VALUE)

                result shouldBe ORIGINAL_VALUE
            }

            "calling orElse function should return a value" {
                val result = original.orElse { ELSE_VALUE }

                result shouldBe ORIGINAL_VALUE
            }

            "should comply with equals() and hashCode() contract" {
                original.shouldBeEqualsContract(
                    y = JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    z = JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    other = JsResult.Success(location = JsLocation.empty, value = ORIGINAL_VALUE)
                )
            }
        }

        "A JsResult#Failure type" - {
            val original: JsResult<String> = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

            "constructor(JsLocation, JsError)" {
                val failure = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                failure.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(location = LOCATION, errors = JsErrors.of(JsonErrors.PathMissing))
                )
            }

            "constructor(JsLocation, JsErrors)" {
                val errors = JsErrors.of(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                )

                val failure = JsResult.Failure(location = LOCATION, errors = errors)

                failure.causes shouldContainAll listOf(JsResult.Failure.Cause(location = LOCATION, errors = errors))
            }

            "calling plus function should return " {
                val firstFailure = JsResult.Failure(location = LOCATION, errors = JsErrors.of(JsonErrors.PathMissing))
                val secondFailure = JsResult.Failure(
                    location = LOCATION,
                    errors = JsErrors.of(
                        JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                    )
                )

                val failure = firstFailure + secondFailure

                failure.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(location = LOCATION, errors = JsErrors.of(JsonErrors.PathMissing)),
                    JsResult.Failure.Cause(
                        location = LOCATION,
                        errors = JsErrors.of(
                            JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                        )
                    )
                )
            }

            "calling map function should return an original do not apply the [transform] function to the value" {
                val result = original.map { it.toInt() }

                result shouldBe original
            }

            "calling flatMap function should return an original do not apply the [transform] function to the value" {
                val result = original.flatMap { location, value -> JsResult.Success(location, value.toInt()) }

                result shouldBe original
            }

            "calling recovery function should return the result of invoking the recovery function" {
                val result = original.recovery { _ -> JsResult.Success(LOCATION, ELSE_VALUE) }

                result shouldBe JsResult.Success(location = LOCATION, value = ELSE_VALUE)
            }

            "calling getOrElse function should return a defaultValue" {
                val result = original.getOrElse(ELSE_VALUE)

                result shouldBe ELSE_VALUE
            }

            "calling orElse function should return the result of calling the [defaultValue] function" {
                val result = original.orElse { ELSE_VALUE }

                result shouldBe ELSE_VALUE
            }

            "should comply with equals() and hashCode() contract" {
                original.shouldBeEqualsContract(
                    y = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    z = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    other = JsResult.Failure(location = JsLocation.empty, error = JsonErrors.PathMissing)
                )
            }
        }

        "A JsResult#Failure#Cause type" - {

            "constructor(JsLocation, JsError)" {
                val cause = JsResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)

                cause.location shouldBe LOCATION
                cause.errors shouldBe JsErrors.of(JsonErrors.PathMissing)
            }

            "constructor(JsLocation, JsErrors)" {
                val cause = JsResult.Failure.Cause(
                    location = LOCATION,
                    errors = JsErrors.of(
                        JsonErrors.PathMissing,
                        JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                    )
                )

                cause.location shouldBe LOCATION
                cause.errors shouldContainAll listOf(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                )
            }

            "should comply with equals() and hashCode() contract" {
                val cause = JsResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)

                cause.shouldBeEqualsContract(
                    y = JsResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing),
                    z = JsResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing),
                    other = JsResult.Failure.Cause(location = JsLocation.empty, error = JsonErrors.PathMissing)
                )
            }
        }

        "JsResult#merge function" {
            val failures = listOf(
                JsResult.Failure(location = LOCATION, errors = JsErrors.of(JsonErrors.PathMissing)),
                JsResult.Failure(
                    location = LOCATION,
                    errors = JsErrors.of(
                        JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                    )
                )
            )

            val failure = failures.merge()

            failure.causes shouldContainAll listOf(
                JsResult.Failure.Cause(location = LOCATION, errors = JsErrors.of(JsonErrors.PathMissing)),
                JsResult.Failure.Cause(
                    location = LOCATION,
                    errors = JsErrors.of(
                        JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                    )
                )
            )
        }

        "asSuccess(JsLocation) extension function" {
            val result = ORIGINAL_VALUE.asSuccess(LOCATION)

            result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
        }

        "asFailure(JsLocation) extension function" {
            val result = JsonErrors.PathMissing.asFailure(LOCATION)

            result as JsResult.Failure

            result.causes shouldContainAll listOf(
                JsResult.Failure.Cause(
                    location = LOCATION,
                    errors = JsErrors.of(JsonErrors.PathMissing)
                )
            )
        }
    }
}
