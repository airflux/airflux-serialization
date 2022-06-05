package io.github.airflux.dsl.reader.`object`.property.path

import io.github.airflux.core.path.JsPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainInOrder

internal class JsPathsOperatorsTest : FreeSpec() {

    companion object {
        private val pathUser = JsPath("user")
        private val pathId = JsPath("id")
        private val pathName = JsPath("name")
    }

    init {
        "The JsPaths type" - {
            val paths = JsPaths(pathUser)

            "JsPaths#or(JsPath) function" - {
                val updatedPath = paths or pathId

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainInOrder listOf(pathUser, pathId)
                }
            }

            "JsPaths#or(JsPaths) function" - {
                val updatedPath = paths or JsPaths(pathId, pathName)

                "should have elements in the order they were passed element" {
                    updatedPath.items shouldContainInOrder listOf(pathUser, pathId, pathName)
                }
            }
        }
    }
}
