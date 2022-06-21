package io.github.airflux.core.reader.result

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.kotest.shouldBeEqualsContract
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should

internal class JsErrorsTest : FreeSpec() {

    init {

        "A JsErrors type" - {

            "#invoke(JsError, _) should return JsErrors with a single error" {
                val errors = JsErrors(JsonErrors.PathMissing)

                errors.items shouldContainAll listOf(JsonErrors.PathMissing)
            }

            "#invoke(JsError, JsError) should return JsErrors with all errors" {
                val errors = JsErrors(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING)
                )

                errors.items shouldContainAll listOf(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING)
                )
            }

            "#invoke(List<JsError>)" - {

                "should return null if list is empty" {
                    val errors = JsErrors(emptyList())

                    errors should beNull()
                }

                "should return JsErrors with errors from the list" {
                    val errors = JsErrors(
                        listOf(
                            JsonErrors.PathMissing,
                            JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING)
                        )
                    )

                    errors.shouldNotBeNull()
                        .items.shouldContainAll(
                            listOf(
                                JsonErrors.PathMissing,
                                JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING)
                            )
                        )
                }
            }

            "calling plus function should return a new JsErrors object with all errors" {
                val firstErrors = JsErrors(JsonErrors.PathMissing)
                val secondErrors = JsErrors(JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING))

                val errors = firstErrors + secondErrors

                errors.items shouldContainAll listOf(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING)
                )
            }

            "should comply with equals() and hashCode() contract" {
                val errors = JsErrors(JsonErrors.PathMissing)

                errors.shouldBeEqualsContract(
                    y = JsErrors(JsonErrors.PathMissing),
                    z = JsErrors(JsonErrors.PathMissing),
                    other = JsErrors(JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING))
                )
            }
        }
    }
}
