package io.github.airflux.core.path

import io.github.airflux.core.common.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class JsPathTest : FreeSpec() {

    companion object {
        private val OTHER_PATH = JsPath("other")
    }

    init {
        "A 'JsPath' type" - {

            val user = "user"
            "create from named path element '$user'" - {
                val path = JsPath(user)

                "should have only one element" {
                    path.size shouldBe 1
                }

                "should have element of type 'PathElement.Key' with value '$user'" {
                    path[0].shouldBeInstanceOf<PathElement.Key>().key shouldBe user
                }

                "method 'toString() should return '#/$user'" {
                    path.toString() shouldBe "#/$user"
                }

                "should comply with equals() and hashCode() contract" {
                    path.shouldBeEqualsContract(y = JsPath(user), z = JsPath(user), other = OTHER_PATH)
                }

                val name = "name"
                "append named path element '$name'" - {
                    val updatedPath = path.append(name)

                    "should have two elements"{
                        updatedPath.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath shouldContainInOrder listOf(PathElement.Key(user), PathElement.Key(name))
                    }

                    "method 'toString() should return '#/$user/$name'" {
                        updatedPath.toString() shouldBe "#/$user/$name"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = JsPath(user).append(name),
                            z = JsPath(user).append(name),
                            other = OTHER_PATH
                        )
                    }
                }

                val idx = 0
                "append index path element '$idx'" - {
                    val updatedPath = path.append(idx)

                    "should have two elements"{
                        updatedPath.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath shouldContainInOrder listOf(PathElement.Key(user), PathElement.Idx(idx))
                    }

                    "method 'toString() should return '#/$user[$idx]'" {
                        updatedPath.toString() shouldBe "#/$user[$idx]"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = JsPath(user).append(idx),
                            z = JsPath(user).append(idx),
                            other = OTHER_PATH
                        )
                    }
                }
            }

            val firstIdx = 0
            "create from index path element '$firstIdx'" - {
                val path = JsPath(firstIdx)

                "should have only one element" {
                    path.size shouldBe 1
                }

                "should have element of type 'PathElement.Idx' with value '$firstIdx'" {
                    path[0].shouldBeInstanceOf<PathElement.Idx>().idx shouldBe firstIdx
                }

                "method 'toString() should return '#[$firstIdx]'" {
                    path.toString() shouldBe "#[$firstIdx]"
                }

                "should comply with equals() and hashCode() contract" {
                    path.shouldBeEqualsContract(y = JsPath(firstIdx), z = JsPath(firstIdx), other = OTHER_PATH)
                }

                val name = "name"
                "append named path element '$name'" - {
                    val updatedPath = path.append(name)

                    "should have two elements"{
                        updatedPath.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath shouldContainInOrder listOf(PathElement.Idx(firstIdx), PathElement.Key(name))
                    }

                    "method 'toString() should return '#[$firstIdx]/$name'" {
                        updatedPath.toString() shouldBe "#[$firstIdx]/$name"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = JsPath(firstIdx).append(name),
                            z = JsPath(firstIdx).append(name),
                            other = OTHER_PATH
                        )
                    }
                }

                val secondIdx = 1
                "append index path element '$secondIdx'" - {
                    val updatedPath = path.append(secondIdx)

                    "should have two elements"{
                        updatedPath.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath shouldContainInOrder listOf(PathElement.Idx(firstIdx), PathElement.Idx(secondIdx))
                    }

                    "method 'toString() should return '#[$firstIdx][$secondIdx]'" {
                        updatedPath.toString() shouldBe "#[$firstIdx][$secondIdx]"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = JsPath(firstIdx).append(secondIdx),
                            z = JsPath(firstIdx).append(secondIdx),
                            other = OTHER_PATH
                        )
                    }
                }
            }
        }
    }
}
