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

Define the environment to deserialization of some domain type using the reader

```kotlin
val readerEnv = JsReaderEnv(
    errorBuilders = ReaderErrorBuilders,
    options = ReaderOptions(failFast = true)
)
```

- Define error builders for the reading environment

```kotlin
object ReaderErrorBuilders : InvalidTypeErrorBuilder,
                             PathMissingErrorBuilder,
                             NumberFormatErrorBuilder,
                             IsNotBlankStringValidator.ErrorBuilder,
                             PatternStringValidator.ErrorBuilder,
                             IsNotEmptyStructValidator.ErrorBuilder,
                             AdditionalPropertiesStructValidator.ErrorBuilder,
                             AdditionalItemsErrorBuilder,
                             IsNotEmptyArrayValidator.ErrorBuilder,
                             MinimumNumberValidator.ErrorBuilder {

    //Reading error builders
    override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
        JsonErrors.InvalidType(expected = expected, actual = actual)

    override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing

    override fun numberFormatError(value: String, target: KClass<*>): JsReaderResult.Error =
        JsonErrors.NumberFormat(value, target)

    //String validation error builders
    override fun isNotBlankStringError(): JsReaderResult.Error = JsonErrors.Validation.Strings.IsBlank

    override fun patternStringError(value: String, pattern: Regex): JsReaderResult.Error =
        JsonErrors.Validation.Strings.Pattern(value, pattern)

    //Object validation error builders
    override fun isNotEmptyStructError(): JsReaderResult.Error = JsonErrors.Validation.Struct.IsEmpty

    override fun additionalPropertiesStructError(): JsReaderResult.Error =
        JsonErrors.Validation.Struct.AdditionalProperties

    //Array validation error builders
    override fun isNotEmptyArrayError(): JsReaderResult.Error = JsonErrors.Validation.Arrays.IsEmpty

    override fun additionalItemsError(): JsReaderResult.Error = JsonErrors.Validation.Arrays.AdditionalItems

    //Number validation error builders
    override fun minimumNumberError(expected: Number, actual: Number): JsReaderResult.Error =
        JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual)
}
```

- Define parsing and validation errors

```kotlin
sealed class JsonErrors : JsReaderResult.Error {
    data object PathMissing : JsonErrors()
    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()
    data class NumberFormat(val value: String, val type: KClass<*>) : JsonErrors()

    sealed class Validation : JsonErrors() {
        sealed class Struct : Validation() {
            data object IsEmpty : Struct()
            data object AdditionalProperties : Struct()
        }

        sealed class Arrays : Validation() {
            data object IsEmpty : Arrays()
            data object AdditionalItems : Arrays()
        }

        sealed class Strings : Validation() {
            data object IsBlank : Strings()
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

#### Define readers for domain types

- Define the generic readers

```kotlin
// Primitive type readers
val intReader = IntReader<ReaderErrorBuilders, ReaderOptions>()
val stringReader = StringReader<ReaderErrorBuilders, ReaderOptions>()

// The generic reader for the id property
val positiveNumberReader: JsReader<ReaderErrorBuilders, ReaderOptions, Int> =
    intReader.validation(StdNumberValidator.minimum(0))

// The generic reader for the username property
val nonEmptyStringReader: JsReader<ReaderErrorBuilders, ReaderOptions, String> =
    stringReader.validation(isNotBlank)

// The reader for the phone number property
val phoneNumberReader: JsReader<ReaderErrorBuilders, ReaderOptions, String> =
    nonEmptyStringReader.validation(StdStringValidator.pattern("\\d*".toRegex()))
```

- Define the validators

```kotlin
//String validators
val isNotBlank = StdStringValidator.isNotBlank<ReaderErrorBuilders, ReaderOptions>()

//Object validators
fun additionalProperties(properties: StructProperties<ReaderErrorBuilders, ReaderOptions>) =
    StdStructValidator.additionalProperties(properties)

//Array validators
val isNotEmptyArray = StdArrayValidator.isNotEmpty<ReaderErrorBuilders, ReaderOptions>()
```

- Define the reader for the Phone type

```kotlin
val phoneReader: JsReader<ReaderErrorBuilders, ReaderOptions, Phone> = structReader {
    validation { properties -> additionalProperties(properties) }

    val title = property(required(name = "title", reader = nonEmptyStringReader))
    val number = property(required(name = "number", reader = phoneNumberReader))

    returns { _, location ->
        Phone(title = +title, number = +number).toSuccess(location)
    }
}
```

- Define the reader for the Phones type

```kotlin
val phonesReader: JsReader<ReaderErrorBuilders, ReaderOptions, Phones> = arrayReader {
    validation(isNotEmptyArray)
    returns(items = phoneReader)
}.map { phones -> Phones(phones) }
```

- Define the reader for the User type

```kotlin
val userReader: JsReader<ReaderErrorBuilders, ReaderOptions, User> = structReader {
    val id = property(required(name = "id", reader = positiveNumberReader))
    val name = property(required(name = "name", reader = nonEmptyStringReader))
    val phones = property(optional(name = "phones", reader = phonesReader, default = { _, _ -> Phones() }))

    returns { _, location ->
        User(id = +id, name = +name, phones = +phones).toSuccess(location)
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
    JsWriterEnv(options = WriterOptions(writerActionIfResultIsEmpty = WriterActionIfResultIsEmpty.RETURN_NOTHING))
```

- Define the options for the writing environment

```kotlin
class WriterOptions(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
    WriterActionBuilderIfResultIsEmptyOption
```

#### Define the writers for some domain types

- Define the generic writers

```kotlin
val StringWriter = stringWriter<WriterOptions>()
val IntWriter = intWriter<WriterOptions>()
```

- Define the writer for the Phone type

```kotlin
val PhoneWriter: JsWriter<WriterOptions, Phone> = structWriter {
    property(nonNullable(name = "title", from = Phone::title, writer = StringWriter))
    property(nonNullable(name = "number", from = Phone::number, writer = StringWriter))
}
```

- Define the writer for the Phones type

```kotlin
val PhonesWriter: JsWriter<WriterOptions, Iterable<Phone>> = arrayWriter(PhoneWriter)
```

- Define the writer for the User type

```kotlin
val UserWriter: JsWriter<WriterOptions, User> = structWriter {
    property(nonNullable(name = "id", from = User::id, writer = IntWriter))
    property(nonNullable(name = "name", from = User::name, writer = StringWriter))
    property(nonNullable(name = "phones", from = User::phones, writer = PhonesWriter))
}
```
