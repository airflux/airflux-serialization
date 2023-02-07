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
    context = readerCtx,
    reader = UserReader
).orThrow { IllegalStateException() }
```

#### Define the environment for reading

Define the environment to deserialization of some domain type using the reader

```kotlin
val readerEnv = ReaderEnv(
    errorBuilders = ReaderErrorBuilders,
    options = ReaderOptions(failFast = true)
)
```

- Define error builders for the reading environment

```kotlin
object ReaderErrorBuilders : InvalidTypeErrorBuilder,
                             PathMissingErrorBuilder,
                             ValueCastErrorBuilder,
                             IsNotBlankStringValidator.ErrorBuilder,
                             PatternStringValidator.ErrorBuilder,
                             IsNotEmptyStructValidator.ErrorBuilder,
                             AdditionalPropertiesStructValidator.ErrorBuilder,
                             AdditionalItemsErrorBuilder,
                             IsNotEmptyArrayValidator.ErrorBuilder,
                             MinimumNumberValidator.ErrorBuilder {

    //Reading error builders
    override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
        JsonErrors.InvalidType(expected = expected, actual = actual)

    override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing

    override fun valueCastError(value: String, target: KClass<*>): ReaderResult.Error =
        JsonErrors.ValueCast(value, target)

    //String validation error builders
    override fun isNotBlankStringError(): ReaderResult.Error = JsonErrors.Validation.Strings.IsBlank

    override fun patternStringError(value: String, pattern: Regex): ReaderResult.Error =
        JsonErrors.Validation.Strings.Pattern(value, pattern)

    //Object validation error builders
    override fun isNotEmptyStructError(): ReaderResult.Error = JsonErrors.Validation.Struct.IsEmpty

    override fun additionalPropertiesStructError(): ReaderResult.Error =
        JsonErrors.Validation.Struct.AdditionalProperties

    //Array validation error builders
    override fun isNotEmptyArrayError(): ReaderResult.Error = JsonErrors.Validation.Arrays.IsEmpty

    override fun additionalItemsError(): ReaderResult.Error = JsonErrors.Validation.Arrays.AdditionalItems

    //Number validation error builders
    override fun minimumNumberError(expected: Number, actual: Number): ReaderResult.Error =
        JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual)
}
```

- Define parsing and validation errors

```kotlin
sealed class JsonErrors : ReaderResult.Error {
    object PathMissing : JsonErrors()
    data class InvalidType(val expected: Iterable<String>, val actual: String) : JsonErrors()
    data class ValueCast(val value: String, val type: KClass<*>) : JsonErrors()

    sealed class Validation : JsonErrors() {
        sealed class Struct : Validation() {
            object IsEmpty : Struct()
            object AdditionalProperties : Struct()
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
            class Min<T>(val expected: T, val actual: T) : Numbers()
        }
    }
}
```

- Define the options for the reading environment

```kotlin
class ReaderOptions(override val failFast: Boolean) : FailFastOption
```

#### Define the context for reading

```kotlin
val readerCtx = ReaderCtx("123")
```

- Define the context for the reading environment

```kotlin
class ReaderCtx(val id: String)
```

#### Define readers for domain types

- Define the generic readers

```kotlin
// Primitive type readers
val IntReader = intReader<ReaderErrorBuilders, ReaderOptions, ReaderCtx>()
val StringReader = stringReader<ReaderErrorBuilders, ReaderOptions, ReaderCtx>()

// The generic reader for the id property
val PositiveNumberReader: Reader<ReaderErrorBuilders, ReaderOptions, ReaderCtx, Int> =
    IntReader.validation(StdNumberValidator.minimum(0))

// The generic reader for the username property
val NonEmptyStringReader: Reader<ReaderErrorBuilders, ReaderOptions, ReaderCtx, String> =
    StringReader.validation(isNotBlank)

// The reader for the phone number property
val PhoneNumberReader: Reader<ReaderErrorBuilders, ReaderOptions, ReaderCtx, String> =
    NonEmptyStringReader.validation(StdStringValidator.pattern("\\d*".toRegex()))
```

- Define the validators
```kotlin
//String validators
val isNotBlank = StdStringValidator.isNotBlank<ReaderErrorBuilders, ReaderOptions, ReaderCtx>()

//Object validators
val additionalProperties = StdStructValidator.additionalProperties<ReaderErrorBuilders, ReaderOptions, ReaderCtx>()

//Array validators
val isNotEmptyArray = StdArrayValidator.isNotEmpty<ReaderErrorBuilders, ReaderOptions, ReaderCtx>()
```

- Define the reader for the Phone type

```kotlin
val PhoneReader: Reader<ReaderErrorBuilders, ReaderOptions, ReaderCtx, Phone> = structReader {
    validation(additionalProperties)

    val title = property(required(name = "title", reader = NonEmptyStringReader))
    val number = property(required(name = "number", reader = PhoneNumberReader))

    returns { _, _, location ->
        Phone(title = +title, number = +number).success(location)
    }
}
```

- Define the reader for the Phones type

```kotlin
val PhonesReader: Reader<ReaderErrorBuilders, ReaderOptions, ReaderCtx, Phones> = arrayReader {
    validation(isNotEmptyArray)
    returns(items = nonNullable(PhoneReader))
}.map { phones -> Phones(phones) }
```

- Define the reader for the User type

```kotlin
val UserReader: Reader<ReaderErrorBuilders, ReaderOptions, ReaderCtx, User> = structReader {

    val id = property(required(name = "id", reader = PositiveNumberReader))
    val name = property(required(name = "name", reader = NonEmptyStringReader))
    val phones = property(optional(name = "phones", reader = PhonesReader, default = { _, _ -> Phones() }))

    returns { _, _, location ->
        User(id = +id, name = +name, phones = +phones).success(location)
    }
}
```

## Writing

### Serialization the User object

```kotlin
val user = User(id = 42, name = "user", phones = Phones(listOf(Phone(title = "mobil", number = "123456789"))))
val json = user.serialization(mapper = mapper, env = writerEnv, context = WriterCtx, writer = UserWriter)
```

#### Define the environment to serialization of some domain type using the writer

```kotlin
val writerEnv =
    WriterEnv(options = WriterOptions(writerActionIfResultIsEmpty = WriterActionIfResultIsEmpty.RETURN_NOTHING))
```

- Define the options for the writing environment

```kotlin
class WriterOptions(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
    WriterActionBuilderIfResultIsEmptyOption
```

#### Define the context to serialization of some domain type using the writer

```kotlin
object WriterCtx
```

#### Define the writers for some domain types

- Define the generic writers

```kotlin
val StringWriter = stringWriter<WriterOptions, WriterCtx>()
val IntWriter = intWriter<WriterOptions, WriterCtx>()
```

- Define the writer for the Phone type

```kotlin
val PhoneWriter: Writer<WriterOptions, WriterCtx, Phone> = structWriter {
    property(nonNullable(name = "title", from = Phone::title, writer = StringWriter))
    property(nonNullable(name = "number", from = Phone::number, writer = StringWriter))
}
```

- Define the writer for the Phones type

```kotlin
val PhonesWriter: Writer<WriterOptions, WriterCtx, Iterable<Phone>> = arrayWriter {
    items(nullable(PhoneWriter))
}
```

- Define the writer for the User type

```kotlin
val UserWriter: Writer<WriterOptions, WriterCtx, User> = structWriter {
    property(nonNullable(name = "id", from = User::id, writer = IntWriter))
    property(nonNullable(name = "name", from = User::name, writer = StringWriter))
    property(nonNullable(name = "phones", from = User::phones, writer = PhonesWriter))
}
```
