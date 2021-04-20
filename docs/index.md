# The AirFlux JSON library.

![CI](https://github.com/airflux/airflux/workflows/CI/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=alert_status)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=bugs)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=coverage)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=airflux_airflux)

The library to parse, validate and generate data in the JSON (JavaScript Object Notation) format.

JSON sample
```json
{
  "tender": {
    "id": "tender-md-0000-1234",
    "title": "Title of the tender.",
    "lots": [
      {
        "id": "lot-1",
        "status": "active",
        "value": {
          "amount": 123.78,
          "currency": "USD"
        }
      }
    ]
  }
}
```

Data classes
```kotlin

data class Request(val tender: Tender)
data class Tender(val id: String, val title: String, val value: Value?, val lots: List<Lot>)
data class Lot(val id: String, val status: LotStatus, val value: Value)
data class Value(val amount: BigDecimal, val currency: String)
```

## JsPath
JsPath represents the location of data in a JsValue structure.

Simple path
```kotlin
val tenderTitlePath = "tender" / "title"
```
Indexed path
```kotlin
val firstLotValuePath = "tender" / "lots" / 0 / "value"
```

## JsReader

### Example 1
#### Define errors builder.
```kotlin
object ErrorBuilder {
    val PathMissing: () -> JsError = { JsonErrors.PathMissing }
    val InvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError = JsonErrors::InvalidType
}
```

#### Define base readers.
- Define readers for primitive types.
```kotlin
object PrimitiveReader : BasePrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}
```

- Define path-readers.
```kotlin
object PathReaders {

    fun <T : Any> readRequired(from: JsValue, byPath: JsPath, using: JsReader<T>): JsResult<T> =
        readRequired(from, byPath, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> readRequired(from: JsValue, byName: String, using: JsReader<T>): JsResult<T> =
        readRequired(from, byName, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> readOptional(from: JsValue, byPath: JsPath, using: JsReader<T>): JsResult<T?> =
        readOptional(from, byPath, using, ErrorBuilder.InvalidType)

    fun <T : Any> readOptional(from: JsValue, byName: String, using: JsReader<T>): JsResult<T?> =
        readOptional(from, byName, using, ErrorBuilder.InvalidType)
}
```

- Define collection readers.
```kotlin
object CollectionReaders {
    fun <T : Any> readAsList(using: JsReader<T>): JsReader<List<T>> =
        readAsList(using, ErrorBuilder.InvalidType)
}
```

- Define reader of enum.
```kotlin
object EnumReader {
    inline fun <reified T : Enum<T>> readAsEnum(): JsReader<T> =
        JsReader { input ->
            stringReader.read(input)
                .validation(StringValidator.isNotBlank)
                .flatMap { text ->
                    try {
                        JsResult.Success(enumValueOf<T>(text.toUpperCase()))
                    } catch (ignored: Exception) {
                        JsResult.Failure(JsonErrors.EnumCast(actual = text, expected = enumValues<T>().joinToString()))
                    }
                }
        }
}
```

#### Define domain readers.

- Define reader for a 'Amount' type.
```kotlin
val AmountReader = PrimitiveReader.bigDecimalReader
```

- Define reader for a 'Value' type.
```kotlin
val ValueReader: JsReader<Value> = run {
    val amountMoreZero = gt(BigDecimal.ZERO)

    reader { input ->
        JsResult.Success(
            Value(
                amount = readRequired(from = input, byPath = JsPath.empty / "amount", using = AmountReader)
                    .validation(amountMoreZero)
                    .onFailure { return@reader it },

                currency = readRequired(from = input, byPath = JsPath.empty / "currency", using = stringReader)
                    .validation(isNotBlank)
                    .onFailure { return@reader it }
            )
        )
    }
}
```

- Define reader for a 'LotStatus' type.
```kotlin
val LotStatusReader: JsReader<LotStatus> = EnumReader.readAsEnum<LotStatus>()
```

- Define reader for a 'Lot' type.
```kotlin
val LotReader: JsReader<Lot> = reader { input ->
    JsResult.fx {
        val (id) = readRequired(from = input, byName = "id", using = stringReader)
            .validation(isNotBlank)
        val (status) = readRequired(from = input, byPath = JsPath.empty / "status", using = LotStatusReader)
        val (value) = readRequired(from = input, byPath = JsPath.empty / "value", using = ValueReader)

        Lot(id = id, status = status, value = value)
    }
}
```

- Define reader for a 'Lots' type.
```kotlin
val LotsReader = CollectionReader.list(LotReader)
    .validation(isUnique { lot -> lot.id })
```

- Define reader for a 'Tender' type.
```kotlin
val TenderReader: JsReader<Tender> = run {

    val titleIsNotEmpty = applyIfNotNull(isNotBlank)

    reader { input ->
        JsResult.Success(
            Tender(
                id = readRequired(from = input, byName = "id", using = stringReader)
                    .validation(isNotBlank)
                    .onFailure { return@reader it },
                title = readOptional(from = input, byName = "title", using = stringReader)
                    .validation(titleIsNotEmpty)
                    .onFailure { return@reader it },
                value = readOptional(from = input, byPath = JsPath.empty / "value", using = ValueReader)
                    .onFailure { return@reader it },
                lots = readRequired(from = input, byPath = JsPath.empty / "lots", using = LotsReader)
                    .onFailure { return@reader it }
            )
        )
    }
}
```

- Define reader for a 'Request' type.
```kotlin
val RequestReader: JsReader<Request> = reader { input ->
    JsResult.Success(
        Request(
            tender = readRequired(from = input, byPath = JsPath.empty / "tender", using = TenderReader)
                .onFailure { return@reader it })
    )
}
```

### Example 2

#### Define errors builder.
```kotlin
object ErrorBuilder {
    val PathMissing: () -> JsError = { JsonErrors.PathMissing }
    val InvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError = JsonErrors::InvalidType
}
```

#### Define base readers.
- Define readers for primitive types.
```kotlin
object PrimitiveReader : BasePrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}
```

- Define collection readers.
```kotlin
object CollectionReaders {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = JsReader { input ->
        readAsList(input, using, ErrorBuilder.InvalidType)
    }
}
```

- Define reader of enum.
```kotlin
object EnumReader {
    inline fun <reified T : Enum<T>> readAsEnum(): JsReader<T> =
        JsReader { input ->
            stringReader.read(input)
                .validation(StringValidator.isNotBlank)
                .flatMap { text ->
                    try {
                        JsResult.Success(enumValueOf<T>(text.toUpperCase()))
                    } catch (ignored: Exception) {
                        JsResult.Failure(JsonErrors.EnumCast(actual = text, expected = enumValues<T>().joinToString()))
                    }
                }
        }
}
```

- Define default config for object reader.
```kotlin
val DefaultObjectReaderConfig = ObjectReaderConfiguration.Builder()
    .apply {
        failFast = true
    }
    .build()
```

- Define default validations for object reader.
```kotlin
val DefaultObjectValidations = ObjectValidations.Builder()
    .apply {
        isNotEmpty = true
    }
```

- Define object reader builder.
```kotlin
val reader = ObjectReader(ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)
inline fun <T> simpleBuilder(crossinline builder: (ObjectValuesMap) -> T): (ObjectValuesMap) -> JsResult<T> =
    { JsResult.Success(builder(it)) }
```

#### Define domain readers.

- Define reader for a 'Amount' type.
```kotlin
private val amountMoreZero = NumberValidator.gt(BigDecimal.ZERO)
val AmountReader = bigDecimalReader.validation(amountMoreZero)
```

- Define reader for a 'Currency' type.
```kotlin
val CurrencyReader: JsReader<String> = stringReader.validation(isNotBlank)
```

- Define reader for a 'Value' type.
```kotlin
val ValueReader: JsReader<Value> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val amount = property(name = "amount", reader = AmountReader).required()
    val currency = property(name = "currency", reader = CurrencyReader).required()

    typeBuilder = simpleBuilder { values ->
        Value(
            amount = values[amount],
            currency = values[currency]
        )
    }
}
```

- Define reader for a 'LotStatus' type.
```kotlin
val LotStatusReader: JsReader<LotStatus> = EnumReader.readAsEnum<LotStatus>()
```

- Define reader for a 'Lot' type.
```kotlin
val LotReader: JsReader<Lot> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val id = property(name = "id", reader = stringReader).required()
    val status = property(name = "status", reader = LotStatusReader).required()
    val value = property(name = "value", reader = ValueReader).required()

    typeBuilder = simpleBuilder { values ->
        Lot(
            id = values[id],
            status = values[status],
            value = values[value]
        )
    }
}
```

- Define reader for a 'Lots' type.
```kotlin
val LotsReader = CollectionReader.list(LotReader)
    .validation(isUnique { lot -> lot.id })
```

- Define reader for a 'Title' type.
```kotlin
val TitleReader: JsReader<String> = stringReader.validation(isNotBlank)
```

- Define reader for a 'Tender' type.
```kotlin
val TenderReader: JsReader<Tender> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val id = property(name = "id", reader = stringReader).required().validation(isNotBlank)
    val title = property(name = "title", reader = TitleReader).optional()
    val value = property(name = "value", reader = ValueReader).optional()
    val lots = property(name = "lots", reader = LotsReader).required()

    typeBuilder = simpleBuilder { values ->
        Tender(
            id = values[id],
            title = values[title],
            value = values[value],
            lots = values[lots],
        )
    }
}
```

- Define reader for a 'Request' type.
```kotlin
val RequestReader: JsReader<Request> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val tender = property(name = "tender", reader = TenderReader).required()

    typeBuilder = simpleBuilder { values ->
        Request(tender = values[tender])
    }
}
```

## JsWriter
```kotlin
val DecimalWriter = BasePrimitiveWriter.bigDecimal()
val ValueWriter: JsWriter<Value> = objectWriter {
    writeRequired(from = Value::amount, to = "amount", using = DecimalWriter)
    writeRequired(from = Value::currency, to = "currency", using = BasePrimitiveWriter.string)
}

val value = Value(amount = BigDecimal(125.52), currency = "USD")
val output: JsValue = ValueWriter.write(value)
val json = output.toString()
```
