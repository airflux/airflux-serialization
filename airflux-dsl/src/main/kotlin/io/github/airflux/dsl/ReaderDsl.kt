package io.github.airflux.dsl

import io.github.airflux.dsl.ReaderDsl.reader
import io.github.airflux.path.JsPath
import io.github.airflux.reader.CollectionBuilderFactory
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue

@Suppress("unused")
object ReaderDsl {

    inline fun <T> reader(crossinline block: (JsValue) -> JsResult<T>): JsReader<T> =
        JsReader { input -> block(input) }

    fun <T> read(from: JsValue, using: JsReader<T>): JsResult<T> = using.read(from)

    /**
     * Reads a required field at an attribute name [byName].
     *
     * - If node by attribute name [byName] is not found then returns [JsError.PathMissing]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> readRequired(byName: String, using: JsReader<T>): JsReader<T> =
        readRequired(JsPath(byName), using)

    /**
     * Reads a required field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns [JsError.PathMissing]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> readRequired(byPath: JsPath, using: JsReader<T>): JsReader<T> = JsReader.required(byPath, using)

    /**
     * Reads a optional or nullable field at an attribute name [byName].
     *
     * - If node by attribute name [byName] is not found, then returns [defaultValue]
     * - If node by attribute name [byName] is found with value 'null' then returns [defaultValue]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> readOrDefault(byName: String, using: JsReader<T>, defaultValue: () -> T): JsReader<T> =
        readOrDefault(JsPath(byName), using, defaultValue)

    /**
     * Reads a optional or nullable field at [JsPath].
     *
     * - If any node in [JsPath] is not found, then returns [defaultValue]
     * - If the last node in [JsPath] is found with value 'null' then returns [defaultValue]
     * - If the entire path is found then applies [reader]
     */
    fun <T : Any> readOrDefault(byPath: JsPath, using: JsReader<T>, defaultValue: () -> T): JsReader<T> =
        JsReader.orDefault(byPath, using, defaultValue)

    /**
     * Reads a optional or nullable field at an attribute name [byName].
     *
     * - If node by attribute name [byName] is not found, then returns 'null'
     * - If node by attribute name [byName] is found with value 'null' then returns 'null'
     * - If the entire path is found then applies [reader]
     */
    fun <T> readNullable(byName: String, using: JsReader<T>): JsReader<T?> = readNullable(JsPath(byName), using)

    /**
     * Reads a optional or nullable field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns 'null'
     * - If the last node in [JsPath] is found with value 'null' then returns 'null'
     * - If the entire path is found then applies [reader]
     */
    fun <T> readNullable(byPath: JsPath, using: JsReader<T>): JsReader<T?> = JsReader.nullable(byPath, using)

    /**
     * Reads a optional or nullable field at an attribute name [byName].
     *
     * - If node by attribute name [byName] is not found then returns [defaultValue]
     * - If node by attribute name [byName] is found with value 'null' then returns 'null'
     * - If the entire path is found then applies [reader]
     */
    fun <T> readNullableOrDefault(byName: String, using: JsReader<T>, defaultValue: () -> T): JsReader<T?> =
        readNullableOrDefault(JsPath(byName), using, defaultValue)

    /**
     * Reads a optional or nullable field at [JsPath].
     *
     * - If any node in [JsPath] is not found then returns [defaultValue]
     * - If the last node in [JsPath] is found with value 'null' then returns 'null'
     * - If the entire path is found then applies [reader]
     */
    fun <T> readNullableOrDefault(byPath: JsPath, using: JsReader<T>, defaultValue: () -> T): JsReader<T?> =
        JsReader.nullableOrDefault(byPath, using, defaultValue)

    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> =
        JsReader { input -> JsReader.traversable(input, using, CollectionBuilderFactory.listFactory()) }

    fun <T : Any> set(using: JsReader<T>): JsReader<Set<T>> =
        JsReader { input -> JsReader.traversable(input, using, CollectionBuilderFactory.setFactory()) }
}
