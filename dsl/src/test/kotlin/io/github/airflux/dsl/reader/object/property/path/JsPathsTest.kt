package io.github.airflux.dsl.reader.`object`.property.path

import io.github.airflux.core.path.JsPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe

internal class JsPathsTest : FreeSpec() {

    companion object {
        private val pathUser = JsPath("user")
        private val pathId = JsPath("id")
        private val pathName = JsPath("name")
    }

    init {

        "The JsPaths type" - {
            val paths = JsPaths(pathUser)

            "after creating" - {

                "should be non-empty" {
                    paths.items.isEmpty() shouldBe false
                }

                "should have size 1" {
                    paths.items.size shouldBe 1
                }

                "should have elements in the order they were passed" {
                    paths.items shouldContainInOrder listOf(pathUser)
                }

                "method 'toString() should return '[$pathUser]'" {
                    paths.toString() shouldBe "[$pathUser]"
                }

                "and adding some path" - {
                    val updatedPaths = paths.append(pathId)

                    "should be non-empty" {
                        updatedPaths.items.isEmpty() shouldBe false
                    }

                    "should have size 2" {
                        updatedPaths.items.size shouldBe 2
                    }

                    "should have elements in the order they were passed" {
                        updatedPaths.items shouldContainInOrder listOf(pathUser, pathId)
                    }

                    "method 'toString() should return '[$pathUser, $pathId]'" {
                        updatedPaths.toString() shouldBe "[$pathUser, $pathId]"
                    }
                }

                "and adding some paths" - {
                    val updatedPaths = paths.append(JsPaths(pathId, pathName))

                    "should be non-empty" {
                        updatedPaths.items.isEmpty() shouldBe false
                    }

                    "should have size 3" {
                        updatedPaths.items.size shouldBe 3
                    }

                    "should have elements in the order they were passed" {
                        updatedPaths.items shouldContainInOrder listOf(pathUser, pathId, pathName)
                    }

                    "method 'toString() should return '[$pathUser, $pathId, $pathName]'" {
                        updatedPaths.toString() shouldBe "[$pathUser, $pathId, $pathName]"
                    }
                }
            }

            "calling the fold function should return a folding value" {
                val result = JsPaths(pathUser)
                    .append(pathId)
                    .fold({ path -> path.toString() }) { acc, path -> "$acc, $path" }

                result shouldBe "#/user, #/id"
            }
        }
    }
}
