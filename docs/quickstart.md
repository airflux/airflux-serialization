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

### JSON

The example of JSON to be parsing, validation and matching to some types.

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
val parsedUser = JSON.deserialization(mapper = mapper, context = DefaultReaderContext, reader = UserReader)
    .getOrNull()
```

#### Define the readers for domain type.

- Define the generic readers

```kotlin
// The generic reader for the id property
val PositiveNumberReader: Reader<Int> = IntReader.validation(StdComparisonValidator.gt(0))

// The generic reader for the username property
val NonEmptyStringReader: Reader<String> = StringReader.validation(StdStringValidator.isNotBlank)

// The reader for the phone number property
val PhoneNumberReader: Reader<String> = NonEmptyStringReader.validation(StdStringValidator.pattern("\\d*".toRegex()))
```

- Define reader for the Phone type

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

- Define reader for the Phones type

```kotlin
val PhonesReader = arrayReader(ArrayReaderConfiguration) {
    returns(items = nonNullable(PhoneReader))
}.map { phones -> Phones(phones) }
```

- Define reader for the User type

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

#### Define the config for an object reader builder

```kotlin
val ObjectReaderConfiguration = objectReaderConfig {
    validation {
        //Added 'isNotEmpty' validator for an object 
        +StdObjectValidator.isNotEmpty
    }
}
```

#### Define the config for an array reader builder

```kotlin
val ArrayReaderConfiguration = arrayReaderConfig {
    validation {
        //Added 'isNotEmpty' validator for an array
        +StdArrayValidator.isNotEmpty
    }
}
```

#### Define the context for a reader

```kotlin
val DefaultReaderContext = readerContext {
    //Disabled the fail-fast mode
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

## Writing

### Serialization the User object

```kotlin
val user = User(id = 42, name = "user", phones = Phones(listOf(Phone(title = "mobil", number = "123456789"))))
val json = user.serialization(mapper = mapper, context = DefaultWriterContext, writer = UserWriter)
```

#### Define the writers for domain type.

- Define writer for the Phone type

```kotlin
val PhoneWriter = structWriter<Phone> {
    property(nonNullable(name = "title", from = Phone::title, writer = StringWriter))
    property(nonNullable(name = "number", from = Phone::number, writer = StringWriter))
}
```

- Define writer for the Phones type

```kotlin
val PhonesWriter = arrayWriter<Phone> {
    items(nullable(PhoneWriter))
}
```

- Define writer for the User type

```kotlin
val UserWriter = structWriter<User> {
    property(nonNullable(name = "id", from = User::id, writer = IntWriter))
    property(nonNullable(name = "name", from = User::name, writer = StringWriter))
    property(nonNullable(name = "phones", from = User::phones, writer = PhonesWriter))
}
```

#### Define the context for a writer.

```kotlin
val DefaultWriterContext = writerContext()
```
