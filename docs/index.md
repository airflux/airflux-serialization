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
Example 1
```kotlin
val ValueReader: JsReader<Value> = run {
    val amountAttributeReader = readRequired(byName = "amount", using = BasePrimitiveReader.bigDecimal)
    val currencyAttributeReader = readRequired(byName = "currency", using = BasePrimitiveReader.string)
        .validation(isNotBlank())

    reader { input ->
        JsResult.Success(
            Value(
                amount = read(from = input, using = amountAttributeReader).onFailure { return@reader it },
                currency = read(from = input, using = currencyAttributeReader).onFailure { return@reader it }
            )
        )
    }
}
```
Example 2
```kotlin
val LotReader: JsReader<Lot> = run {
    val idAttributeReader = readRequired(byName = "id", using = BasePrimitiveReader.string)
        .validation(isNotBlank())
    val statusAttributeReader = readRequired(byName = "status", using = BasePrimitiveReader.string)
        .validation(isNotBlank())
    val valueAttributeReader = readRequired(byName = "value", using = ValueReader)

    reader { input ->
        JsResult.fx {
            val (id) = read(from = input, using = idAttributeReader)
            val (status) = read(from = input, using = statusAttributeReader)
            val (value) = read(from = input, using = valueAttributeReader)

            Lot(id = id, status = status, value = value)
        }
    }
}
```

## JsWriter
```kotlin
val ValueWriter: JsWriter<Value> = objectWriter {
    writeRequired(from = Value::amount, to = "amount", using = DecimalWriter)
    writeRequired(from = Value::currency, to = "currency", using = BasePrimitiveWriter.string)
}
...
val value = Value(amount = BigDecimal(125.52), currency = "USD")
val output: JsValue = ValueWriter.write(value)
val json = output.toString()
```