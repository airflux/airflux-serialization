# Quickstart

## The domain types

```kotlin
data class User(val id: Int, val name: String, val phones: Phones)
data class Phones(private val items: List<Phone>) : List<Phone> by items
data class Phone(val title: String, val number: String)
```

## Configuration the Jackson

Registering the module 'AirFluxJsonModule' with Jackson to use its parser

```kotlin
val mapper = ObjectMapper().apply {
    registerModule(AirFluxJsonModule)
}
```

## Reading

### The example of JSON

```kotlin
val JSON = """
{
  "id": 42,
  "name": "User",
  "phones": [
    {
      "title": "mobile",
      "number": "+0123456789"
    }
  ]
}
"""
```

### Deserialization JSON

```kotlin
val parsedUser = JSON.deserialization(
    mapper = mapper,
    env = readerEnv,
    reader = UserReader
).orThrow { IllegalStateException() }
```

#### Define the environment for reading

Define the environment to deserialization of some domain type using the reade

```kotlin
val readerEnv = ReaderEnv(
    errorBuilders = ReaderErrorBuilders,
    context = ReaderCtx(failFast = true)
)
```

- Define the parsing and validation errors

```kotlin
sealed class JsonErrors : ReaderResult.Error {
    object PathMissing : JsonErrors()
    data class InvalidType(val expected: ValueNode.Type, val actual: ValueNode.Type) : JsonErrors()
    data class ValueCast(val value: String, val type: KClass<*>) : JsonErrors()

    sealed class Validation : JsonErrors() {
        sealed class Object : Validation() {
            object IsEmpty : Object()
            object AdditionalProperties : Object()
        }

        sealed class Arrays : Validation() {
            object IsEmpty : Arrays()
            object AdditionalItems : Arrays()
        }

        sealed class Strings : Validation() {
            object IsBlank : Strings()
            class Pattern(val value: String, val pattern: Regex) : Strings()
        }

        sealed class Numbers : Validation() {
            class Gt<T>(val expected: T, val actual: T) : Numbers()
        }
    }
}
```

- Define error builders for the reading environment

```kotlin
object ReaderErrorBuilders : InvalidTypeErrorBuilder,
                             PathMissingErrorBuilder,
                             ValueCastErrorBuilder,
                             IsNotBlankStringValidator.ErrorBuilder,
                             PatternStringValidator.ErrorBuilder,
                             IsNotEmptyObjectValidator.ErrorBuilder,
                             AdditionalPropertiesObjectValidator.ErrorBuilder,
                             AdditionalItemsErrorBuilder,
                             IsNotEmptyArrayValidator.ErrorBuilder,
                             GtComparisonValidator.ErrorBuilder {

    //Reading error builders
    override fun invalidTypeError(expected: ValueNode.Type, actual: ValueNode.Type): ReaderResult.Error =
        JsonErrors.InvalidType(expected = expected, actual = actual)

    override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing

    override fun valueCastError(value: String, target: KClass<*>): ReaderResult.Error =
        JsonErrors.ValueCast(value, target)

    //String validation error builders
    override fun isNotBlankStringError(): ReaderResult.Error = JsonErrors.Validation.Strings.IsBlank

    override fun patternStringError(value: String, pattern: Regex): ReaderResult.Error =
        JsonErrors.Validation.Strings.Pattern(value, pattern)

    //Object validation error builders
    override fun isNotEmptyObjectError(): ReaderResult.Error = JsonErrors.Validation.Object.IsEmpty

    override fun additionalPropertiesObjectError(): ReaderResult.Error =
        JsonErrors.Validation.Object.AdditionalProperties

    //Array validation error builders
    override fun isNotEmptyArrayError(): ReaderResult.Error = JsonErrors.Validation.Arrays.IsEmpty

    override fun additionalItemsError(): ReaderResult.Error = JsonErrors.Validation.Arrays.AdditionalItems

    //Comparison validation error builders
    override fun gtComparisonError(expected: Number, actual: Number): ReaderResult.Error =
        JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual)
}
```

- Define the context for the reading environment

```kotlin
class ReaderCtx(override val failFast: Boolean) : FailFastOption
```

#### Define readers for domain types

- Define the generic readers

```kotlin
// Primitive type readers
val IntReader = intReader<ReaderErrorBuilders, ReaderCtx>()
val StringReader = stringReader<ReaderErrorBuilders, ReaderCtx>()

// The generic reader for the id property
val PositiveNumberReader: Reader<ReaderErrorBuilders, ReaderCtx, Int> =
    IntReader.validation(StdComparisonValidator.gt(0))

// The generic reader for the username property
val NonEmptyStringReader: Reader<ReaderErrorBuilders, ReaderCtx, String> =
    StringReader.validation(isNotBlank)

// The reader for the phone number property
val PhoneNumberReader: Reader<ReaderErrorBuilders, ReaderCtx, String> =
    NonEmptyStringReader.validation(StdStringValidator.pattern("\\d*".toRegex()))
```

- Define the reader for the Phone type

```kotlin
val PhoneReader: Reader<ReaderErrorBuilders, ReaderCtx, Phone> = structReader {
    validation {
        +additionalProperties
    }

    val title = property(required(name = "title", reader = NonEmptyStringReader))
    val number = property(required(name = "number", reader = PhoneNumberReader))

    returns { _, _ ->
        Phone(title = +title, number = +number).success()
    }
}
```

- Define the reader for the Phones type

```kotlin
val PhonesReader: Reader<ReaderErrorBuilders, ReaderCtx, Phones> = arrayReader {
    validation {
        +isNotEmptyArray
    }
    returns(items = nonNullable(PhoneReader))
}.map { phones -> Phones(phones) }
```

- Define the reader for the User type

```kotlin
val UserReader: Reader<ReaderErrorBuilders, ReaderCtx, User> = structReader {

    val id = property(required(name = "id", reader = PositiveNumberReader))
    val name = property(required(name = "name", reader = NonEmptyStringReader))
    val phones = property(optionalWithDefault(name = "phones", reader = PhonesReader, default = { _ -> Phones() }))

    returns { _, _ ->
        User(id = +id, name = +name, phones = +phones).success()
    }
}
```

## Writing

### Serialization the User object

```kotlin
val user = User(id = 42, name = "user", phones = Phones(listOf(Phone(title = "mobil", number = "123456789"))))
val json = user.serialization(mapper = mapper, env = writerEnv, writer = UserWriter)
```

#### Define the environment to serialization of some domain type using the writer

```kotlin
val writerEnv =
    WriterEnv(context = WriterCtx(writerActionIfResultIsEmpty = WriterActionIfResultIsEmpty.RETURN_NOTHING))
```

- Define the context for the writing environment

```kotlin
class WriterCtx(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
    WriterActionBuilderIfResultIsEmptyOption
```

#### Define the writers for some domain types

- Define the writer for the Phone type

```kotlin
val PhoneWriter: Writer<WriterCtx, Phone> = structWriter {
    property(nonNullable(name = "title", from = Phone::title, writer = StringWriter))
    property(nonNullable(name = "number", from = Phone::number, writer = StringWriter))
}
```

- Define the writer for the Phones type

```kotlin
val PhonesWriter: Writer<WriterCtx, Iterable<Phone>> = arrayWriter {
    items(nullable(PhoneWriter))
}
```

- Define the writer for the User type

```kotlin
val UserWriter: Writer<WriterCtx, User> = structWriter {
    property(nonNullable(name = "id", from = User::id, writer = IntWriter))
    property(nonNullable(name = "name", from = User::name, writer = StringWriter))
    property(nonNullable(name = "phones", from = User::phones, writer = PhonesWriter))
}
```
