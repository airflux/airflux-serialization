/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.parser.json

internal class PropertyNamePool {
    private var root: Node? = null

    var size: Int = 0
        private set

    fun getOrPut(src: CharBuffer, start: Int, end: Int): String {
        if (root == null) return putNodeAtRoot(src, start, end)

        var node: Node? = root
        var cmp: Int
        var parent: Node

        do {
            parent = node!!
            cmp = node.value.compareWith(src, start, end)
            node = if (cmp < 0)
                node.left
            else if (cmp > 0)
                node.right
            else
                return node.value
        } while (node != null)

        val value = src.buildString(start, end)
        val newNode = Node(value, parent)
        if (cmp < 0)
            parent.left = newNode
        else
            parent.right = newNode

        newNode.rebalance()
        size++
        return value
    }

    private fun putNodeAtRoot(src: CharBuffer, start: Int, end: Int): String {
        val value = src.buildString(start, end)
        root = Node(value, null)
        size = 1
        return value
    }

    private fun String.compareWith(src: CharBuffer, start: Int, end: Int): Int {
        fun String.compareWith(src: CharSequence, start: Int, end: Int): Int {
            var srcPos = start
            var dstPos = 0
            while (srcPos < end && dstPos < this.length) {
                val cmp = this[dstPos].compareTo(src[srcPos])
                if (cmp != 0) return cmp
                srcPos++
                dstPos++
            }
            return 0
        }

        val cmpLength = this.length.compareTo(end - start)
        return if (cmpLength != 0) cmpLength else compareWith(src, start, end)
    }

    private class Node(
        val value: String,
        var parent: Node?
    ) {
        var left: Node? = null
        var right: Node? = null
        var color: Color = Color.BLACK
    }

    private fun Node.rebalance() {
        tailrec fun Node?.rebalance() {
            val x = this
            if (x != null && x !== root && x.parent!!.color == Color.RED) {
                val next = if (x.parentOf() === x.grandParentOf().leftOf())
                    x.rebalanceRight()
                else
                    x.rebalanceLeft()

                next.rebalance()
            } else
                return
        }

        val node: Node = this
        node.color = Color.RED
        node.rebalance()
        root!!.color = Color.BLACK
    }

    private fun Node.rebalanceLeft(): Node? {
        var x: Node = this
        val y = x.grandParentOf().leftOf()
        return if (y.colorOf() == Color.RED) {
            x.parentOf().setBlackColor()
            y.setBlackColor()
            val grandParent = x.grandParentOf()
            grandParent.setRedColor()
            grandParent
        } else {
            if (x === x.parentOf().leftOf()) {
                x = x.parentOf()!!
                x.rotateRight()
            }
            x.parentOf().setBlackColor()
            val grandParent = x.grandParentOf()
            grandParent.setRedColor()
            grandParent.rotateLeft()
            x
        }
    }

    private fun Node.rebalanceRight(): Node? {
        var x: Node = this
        val y = x.grandParentOf().rightOf()
        return if (y.colorOf() == Color.RED) {
            x.parentOf().setBlackColor()
            y.setBlackColor()
            val grandParent = x.grandParentOf()
            grandParent.setRedColor()
            grandParent
        } else {
            if (x === x.parentOf().rightOf()) {
                x = x.parentOf()!!
                x.rotateLeft()
            }
            x.parentOf().setBlackColor()
            val grandParent = x.grandParentOf()
            grandParent.setRedColor()
            grandParent.rotateRight()
            x
        }
    }

    private fun Node?.rotateLeft() {
        val x = this ?: return
        val y = x.right
        x.right = y!!.left
        if (y.left != null)
            y.left!!.parent = x
        y.parent = x.parent
        if (x.parent == null)
            root = y
        else if (x === x.parent!!.left)
            x.parent!!.left = y
        else
            x.parent!!.right = y
        y.left = x
        x.parent = y
    }

    private fun Node?.rotateRight() {
        val x = this ?: return
        val y = x.left
        x.left = y!!.right
        if (y.right != null)
            y.right!!.parent = x
        y.parent = x.parent
        if (x.parent == null)
            root = y
        else if (x === x.parent!!.right)
            x.parent!!.right = y
        else
            x.parent!!.left = y
        y.right = x
        x.parent = y
    }

    private companion object {
        private fun Node?.parentOf(): Node? = this?.parent
        private fun Node?.grandParentOf(): Node? = this?.parent?.parent
        private fun Node?.leftOf(): Node? = this?.left
        private fun Node?.rightOf(): Node? = this?.right
        private fun Node?.colorOf(): Color = this?.color ?: Color.BLACK
        private fun Node?.setRedColor() = setColor(Color.RED)
        private fun Node?.setBlackColor() = setColor(Color.BLACK)
        private fun Node?.setColor(color: Color) {
            if (this != null) this.color = color
        }
    }

    private enum class Color {
        RED, BLACK
    }
}
