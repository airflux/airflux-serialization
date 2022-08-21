# Quickstart

## Reading

### JSON

The example of JSON to be parsing, validation and matching to some types.

```json
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
```

### The domain types

```kotlin
data class User(val id: Int, val name: String, val phones: Phones)
data class Phones(private val items: List<Phone>) : List<Phone> by items
data class Phone(val title: String, val number: String)
```

### Readers

#### Define the parsing and validation errors

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
        }

        sealed class Strings : Validation() {
            object IsEmpty : Strings()
            class Pattern(val value: String, val regex: Regex) : Strings()
        }

        sealed class Numbers : Validation() {
            class Gt<T>(val expected: T, val actual: T) : Numbers()
        }
    }
}
```

#### Define the config for an object reader builder

```kotlin
val ObjectReaderConfiguration = objectReaderConfig {
    validation {
        +StdObjectValidator.isNotEmpty
    }
}
```

#### Define the default context for an object reader

```kotlin
val DefaultReaderContext = readerContext {
    failFast = false

    //registering the object validation error builders
    +IsNotEmptyObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.IsEmpty }
    +AdditionalPropertiesObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.AdditionalProperties }

    //registering the array validation error builders
    +IsNotEmptyArrayValidator.ErrorBuilder { JsonErrors.Validation.Arrays.IsEmpty }

    //registering the string validation error builders
    +IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty }
    +PatternStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::Pattern)

    //registering the number validation error builders
    +GtComparisonValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Gt)

    //registering the parsing error builders
    +PathMissingErrorBuilder { JsonErrors.PathMissing }
    +InvalidTypeErrorBuilder(JsonErrors::InvalidType)
    +ValueCastErrorBuilder(JsonErrors::ValueCast)
}
```

#### Define the config for an array reader builder

```kotlin
val ArrayReaderConfiguration = arrayReaderConfig {
    validation {
        +StdArrayValidator.isNotEmpty
    }
}
```

#### Define the context for an array reader.

```kotlin
val DefaultWriterContext = writerContext()
```

#### Define the readers for domain type.

- The generic readers

```kotlin
// The generic reader for the id property
val PositiveNumberReader: Reader<Int> = IntReader.validation(StdComparisonValidator.gt(0))

// The generic reader for the username property
val NonEmptyStringReader: Reader<String> = StringReader.validation(StdStringValidator.isNotBlank)

// The reader for the phone number property
val PhoneNumberReader: Reader<String> = NonEmptyStringReader.validation(StdStringValidator.pattern("\\d*".toRegex()))
```

- Reader for the Phone type

```kotlin
val PhoneReader = structReader<Phone>(ObjectReaderConfiguration) {
    validation {
        +StdObjectValidator.additionalProperties
    }

    val title = property(required(name = "title", reader = NonEmptyStringReader))
    val number = property(required(name = "number", reader = PhoneNumberReader))

    returns { _, _ ->
        Phone(title = +title, number = +number).success()
    }
}
```

- Reader for the Phones type

```kotlin
val PhonesReader = arrayReader(ArrayReaderConfiguration) {
    returns(items = nonNullable(PhoneReader))
}.map { phones -> Phones(phones) }
```

- Reader for the User type

```kotlin
val UserReader = structReader<User>(ObjectReaderConfiguration) {

    val id = property(required(name = "id", reader = PositiveNumberReader))
    val name = property(required(name = "name", reader = NonEmptyStringReader))
    val phones = property(optionalWithDefault(name = "phones", reader = PhonesReader, default = { _, _ -> Phones() }))

    returns { _, _ ->
        User(id = +id, name = +name, phones = +phones).success()
    }
}
```

## Writing

### Writers

#### Define the config for an object writer builder.

#### Define the context for an object writer.

```kotlin
val DefaultWriterContext = writerContext()
```

#### Define the config for an array writer builder.

#### Define the context for an array writer.

#### Define the writers for domain type.

- Writer for the Phone type

```kotlin
val PhoneWriter = structWriter<Phone> {
    property(nonNullable(name = "title", from = Phone::title, writer = StringWriter))
    property(nonNullable(name = "number", from = Phone::number, writer = StringWriter))
}
```

- Writer for the Phones type

```kotlin
val PhonesWriter = arrayWriter<Phone> {
    items(nullable(PhoneWriter))
}
```

- Writer for the User type

```kotlin
val UserWriter = structWriter<User> {
    property(nonNullable(name = "id", from = User::id, writer = IntWriter))
    property(nonNullable(name = "name", from = User::name, writer = StringWriter))
    property(nonNullable(name = "phones", from = User::phones, writer = PhonesWriter))
}
```