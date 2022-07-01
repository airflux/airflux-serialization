package io.github.airflux.core.reader.result

import io.github.airflux.common.kotest.shouldBeEqualsContract
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.PathElement
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class JsLocationTest : FreeSpec() {

    init {
        "The 'JsLocation' type" - {

            "when empty" - {
                val location = JsLocation.empty

                "should be empty" {
                    location.isEmpty shouldBe true
                }

                "the foldRight() method should perform the fold to the right" {
                    val result: List<PathElement> = JsLocation.foldRight(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<PathElement> = JsLocation.foldLeft(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the append() method with the JsPath type parameter" {
                    val path = JsPath("users").append(0).append("phone")
                    val newLocation = location.append(path)

                    val result: List<PathElement> = JsLocation.foldLeft(mutableListOf(), newLocation) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PathElement.Key("users"),
                        PathElement.Idx(0),
                        PathElement.Key("phone")
                    )
                }

                "the 'toString() method should return '#'" {
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

                "the foldRight() method should perform the fold to the right" {
                    val result: List<PathElement> = JsLocation.foldRight(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PathElement.Key("phone"),
                        PathElement.Idx(0),
                        PathElement.Key("users")
                    )
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<PathElement> = JsLocation.foldLeft(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PathElement.Key("users"),
                        PathElement.Idx(0),
                        PathElement.Key("phone")
                    )
                }

                "the append() method with the JsPath type parameter" {
                    val path = JsPath("mobile").append("title")
                    val newLocation = location.append(path)

                    val result: List<PathElement> = JsLocation.foldLeft(mutableListOf(), newLocation) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PathElement.Key("users"),
                        PathElement.Idx(0),
                        PathElement.Key("phone"),
                        PathElement.Key("mobile"),
                        PathElement.Key("title")
                    )
                }

                "the 'toString() method should return '#/users[0]/phone'" {
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
