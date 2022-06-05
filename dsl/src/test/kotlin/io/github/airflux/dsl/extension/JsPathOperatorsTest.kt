package io.github.airflux.dsl.extension

import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.PathElement
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainInOrder

internal class JsPathOperatorsTest : FreeSpec() {

    init {

        "The JsPath type" - {

            "JsPath#Companion#div(String) function" - {
                val path = JsPath / "user"

                "should have elements in the order they were passed" {
                    path.elements shouldContainInOrder listOf(PathElement.Key("user"))
                }
            }

            "JsPath#Companion#div(Int) function" - {
                val path = JsPath / 0

                "should have elements in the order they were passed" {
                    path.elements shouldContainInOrder listOf(PathElement.Idx(0))
                }
            }

            "JsPath#div(String) function" - {
                val path = JsPath / "user" / "name"

                "should have elements in the order they were passed" {
                    path.elements shouldContainInOrder listOf(PathElement.Key("user"), PathElement.Key("name"))
                }
            }

            "JsPath#div(Int) function" - {
                val path = JsPath / "phones" / 0

                "should have elements in the order they were passed" {
                    path.elements shouldContainInOrder listOf(PathElement.Key("phones"), PathElement.Idx(0))
                }
            }
        }
    }
}
