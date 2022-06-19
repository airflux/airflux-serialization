package io.github.airflux.core.reader.result

import io.github.airflux.common.kotest.shouldBeEqualsContract
import io.github.airflux.core.location.JsLocation
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsLocationTest : FreeSpec() {

    init {
        "A 'JsLocation' type" - {

            "when empty" - {
                val location = JsLocation.empty

                "should be empty" {
                    location.isEmpty shouldBe true
                }

                "method foldRight() should perform the left right" {
                    val result = JsLocation.foldRight("", location) { acc, elem -> acc + elem.toString() }
                    result shouldBe ""
                }

                "method foldLeft() should perform the right left" {
                    val result = JsLocation.foldLeft("", location) { acc, elem -> acc + elem.toString() }
                    result shouldBe ""
                }

                "method 'toString() should return '#'" {
                    location.toString() shouldBe "#"
                }

                "should comply with equals() and hashCode() contract" {
                    location.shouldBeEqualsContract(
                        y = JsLocation.empty,
                        z = JsLocation.empty,
                        other = JsLocation.empty.append("user")
                    )
                }
            }

            "when non-empty" - {
                val location = JsLocation.empty.append("users").append(0).append("phone")

                "should be non-empty" {
                    location.isEmpty shouldBe false
                }

                "method foldRight() should perform the left right" {
                    val result = JsLocation.foldRight("", location) { acc, elem -> acc + elem.toString() }
                    result shouldBe "/phone[0]/users"
                }

                "method foldLeft() should perform the right left" {
                    val result = JsLocation.foldLeft("", location) { acc, elem -> acc + elem.toString() }
                    result shouldBe "/users[0]/phone"
                }

                "method 'toString() should return '#/users[0]/phone'" {
                    location.toString() shouldBe "#/users[0]/phone"
                }

                "should comply with equals() and hashCode() contract" {
                    location.shouldBeEqualsContract(
                        y = JsLocation.empty.append("users").append(0).append("phone"),
                        z = JsLocation.empty.append("users").append(0).append("phone"),
                        other = JsLocation.empty
                    )
                }
            }
        }
    }
}
