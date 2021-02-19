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
data class Lot(val id: String, val status: String, val value: Value)
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

### Define errors builder.
```kotlin
object ErrorBuilder {
    val PathMissing: () -> JsError = { JsonErrors.PathMissing }
    val InvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError = JsonErrors::InvalidType
}
```

### Define base readers.
#### Define readers for primitive types.
```kotlin
object PrimitiveReader : BasePrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}
```
#### Define path-readers.
```kotlin
object PathReaders {

    fun <T : Any> required(from: JsValue, path: JsPath, using: JsReader<T>): JsResult<T> =
        RequiredPathReader.required(from, path, using, ErrorBuilder.PathMissing, ErrorBuilder.InvalidType)

    fun <T : Any> nullable(from: JsValue, path: JsPath, using: JsReader<T>): JsResult<T?> =
        NullablePathReader.nullable(from, path, using, ErrorBuilder.InvalidType)
}
```
#### Define traversable-readers.
```kotlin
object TraversableReaders {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = TraversableReader.list(using, ErrorBuilder.InvalidType)
}
```

### Reader for a 'Value' type.
```kotlin
val ValueReader: JsReader<Value> = run {
    val amountMoreZero = gt(BigDecimal("0.01"))

    reader { input ->
        JsResult.Success(
            Value(
                amount = required(from = input, path = JsPath("amount"), using = bigDecimalReader)
                    .validation(amountMoreZero)
                    .onFailure { return@reader it },

                currency = required(from = input, path = JsPath("currency"), using = stringReader)
                    .validation(isNotBlank)
                    .onFailure { return@reader it }
            )
        )
    }
}
```

### Reader for a 'Lot' type.
```kotlin
val LotReader: JsReader<Lot> = reader { input ->
    JsResult.fx {
        val (id) = required(from = input, path = JsPath("id"), using = stringReader)
            .validation(isNotBlank)
        val (status) = required(from = input, path = JsPath("status"), using = stringReader)
            .validation(isNotBlank)
        val (value) = required(from = input, path = JsPath("value"), using = ValueReader)

        Lot(id = id, status = status, value = value)
    }
}
```

### Reader for a 'Lots' type.
```kotlin
val LotsReader = list(LotReader)
    .validation(isUnique { lot -> lot.id })
```

### Reader for a 'Tender' type.
```kotlin
val TenderReader: JsReader<Tender> = reader { input ->
    JsResult.Success(
        Tender(
            id = required(from = input, path = JsPath("id"), using = stringReader)
                .validation(isNotBlank)
                .onFailure { return@reader it },
            title = required(from = input, path = JsPath("title"), using = stringReader)
                .validation(isNotBlank)
                .onFailure { return@reader it },
            value = nullable(from = input, path = JsPath("value"), using = ValueReader)
                .onFailure { return@reader it },
            lots = required(from = input, path = JsPath("lots"), using = LotsReader)
                .onFailure { return@reader it }
        )
    )
}
```
### Reader for a 'Request' type.
```kotlin
val RequestReader: JsReader<Request> = reader { input ->
    JsResult.Success(
        Request(
            tender = required(from = input, path = JsPath("tender"), using = TenderReader)
                .onFailure { return@reader it })
    )
}
```

## JsWriter
```kotlin
val ValueWriter: JsWriter<Value> = objectWriter {
    writeRequired(from = Value::amount, to = "amount", using = DecimalWriter)
    writeRequired(from = Value::currency, to = "currency", using = BasePrimitiveWriter.string)
}

val value = Value(amount = BigDecimal(125.52), currency = "USD")
val output: JsValue = ValueWriter.write(value)
val json = output.toString()
```
