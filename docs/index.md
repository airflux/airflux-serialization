# The AirFlux JSON library.

![CI](https://github.com/airflux/airflux/workflows/CI/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=alert_status)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=bugs)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=coverage)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=airflux_airflux)

The library to parsing, validate and generate data in the JSON (JavaScript Object Notation) format. The library doesn't
use code generation, annotations or reflection.

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

### Common

#### Define errors builder.

```kotlin
object ErrorBuilder {
    val PathMissing = PathMissingErrorBuilder { JsonErrors.PathMissing }
    val InvalidType = InvalidTypeErrorBuilder { expected, actual ->
        JsonErrors.InvalidType(expected, actual)
    }
}
```

#### Define readers for primitive types.

```kotlin
object PrimitiveReader : BasePrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}
```

#### Define collection readers.

```kotlin
object CollectionReaders {
    fun <T : Any> readAsList(using: JsReader<T>): JsReader<List<T>> =
        readAsList(using, ErrorBuilder.InvalidType)
}
```

#### Define reader of enum.

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

#### Define base readers.

- Define default config for object reader.

```kotlin
private val DefaultObjectReaderConfig = ObjectReaderConfiguration.Builder()
    .apply {
        failFast = true
    }
    .build()
```

- Define default validator builders for object reader.

```kotlin
private val DefaultObjectValidatorBuilders = ObjectValidators.Builder()
    .apply {
        isNotEmpty = true
    }
```

- Define object reader builder.

```kotlin
val reader = ObjectReader(
    initialConfiguration = DefaultObjectReaderConfig,
    initialValidatorBuilders = DefaultObjectValidatorBuilders,
    pathMissingErrorBuilder = ErrorBuilder.PathMissing,
    invalidTypeErrorBuilder = ErrorBuilder.InvalidType
)
inline fun <T> simpleBuilder(crossinline builder: (ObjectValuesMap) -> T): (ObjectValuesMap) -> JsResult<T> =
    { JsResult.Success(builder(it)) }
```

#### Define domain readers.

- Define reader for the 'Amount' type.

```kotlin
private val amountMoreZero = NumberValidator.gt(BigDecimal.ZERO)
val AmountReader = bigDecimalReader.validation(amountMoreZero)
```

- Define reader for the 'Currency' type.

```kotlin
val CurrencyReader: JsReader<String> = stringReader.validation(isNotBlank)
```

- Define reader for the 'Value' type.

```kotlin
val ValueReader = reader<Value> {
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

- Define reader for the 'LotStatus' type.

```kotlin
val LotStatusReader: JsReader<LotStatus> = EnumReader.readAsEnum<LotStatus>()
```

- Define reader for the 'Lot' type.

```kotlin
val LotReader = reader<Lot> {
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

- Define reader for the 'Lots' type.

```kotlin
val LotsReader = CollectionReader.list(LotReader)
    .validation(isUnique { lot -> lot.id })
```

- Define reader for the 'Title' type.

```kotlin
val TitleReader: JsReader<String> = stringReader.validation(isNotBlank)
```

- Define reader for the 'Tender' type.

```kotlin
val TenderReader = reader<Tender> {
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

- Define reader for the 'Request' type.

```kotlin
val RequestReader = reader<Request> {
    val tender = property(name = "tender", reader = TenderReader).required()

    typeBuilder = simpleBuilder { values ->
        Request(tender = values[tender])
    }
}
```

## JsWriter

- Define default config for object writer.

```kotlin
val DefaultObjectWriterConfiguration = ObjectWriterConfiguration()
```

- Define object writer builder.

```kotlin
val writer = ObjectWriter(DefaultObjectWriterConfiguration)
```

#### Define base writers.

- Define writers for primitive types.

```kotlin
val DecimalWriter = BasePrimitiveWriter.bigDecimal()
```

#### Define domain writers.

- Define writer for the 'Value' type.

```kotlin
val ValueWriter: JsWriter<Value> = writer {
    requiredProperty(name = "amount", from = Value::amount, writer = DecimalWriter)
    requiredProperty(name = "currency", from = Value::currency, writer = BasePrimitiveWriter.string)
}
```

- Define writer for the 'LotStatus' type.

```kotlin
val LotStatus = JsWriter<LotStatus> { value ->
    JsString(value.name)
}
```

- Define writer for the 'Lot' type.

```kotlin
val LotWriter: JsWriter<Lot> = writer {
    requiredProperty(name = "id", from = Lot::id, BasePrimitiveWriter.string)
    requiredProperty(name = "status", from = Lot::status, writer = LotStatus)
    requiredProperty(name = "value", from = Lot::value, writer = ValueWriter)
}
```

- Define writer for the 'Lots' type.

```kotlin
val LotsWriter = arrayWriter(LotWriter)
```

- Define writer for the 'Tender' type.

```kotlin
val TenderWriter: JsWriter<Tender> = writer {
    requiredProperty(name = "id", from = Tender::id, writer = BasePrimitiveWriter.string)
    optionalProperty(name = "title", from = Tender::title, writer = BasePrimitiveWriter.string)
    optionalProperty(name = "value", from = Tender::value, writer = ValueWriter)
    optionalProperty(name = "lots", from = Tender::lots, writer = LotsWriter).skipIfEmpty()
}
```

- Define writer for the 'Response' type.

```kotlin
val ResponseWriter: JsWriter<Response> = writer {
    requiredProperty(name = "tender", from = Response::tender, writer = TenderWriter)
}
```

### Using

```kotlin
fun main() {
    val value = Value(amount = BigDecimal("125.52"), currency = "USD")
    val lot = Lot(id = "lot-1", status = LotStatus.ACTIVE, value = value)
    val tender = Tender(id = "tender-1", title = "title", value = value, lots = listOf(lot))
    val response = Response(tender = tender)
    val output: JsValue = ResponseWriter.write(response)
    val json = output.toString()
}
```
