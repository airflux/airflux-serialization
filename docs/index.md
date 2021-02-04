JSON sample
```json
{
  "id": "ade4aa9d-36fe-4c56-8d7f-0ad99d3ecac4",
  "period": {
    "startDate": "2020-05-19T15:33:06Z",
    "endDate": null
  },
  "users": [
    {
      "name": "John",
      "role": "employee",
      "phones": [
        "+12345 678 90 11",
        "+12345 678 90 12"
      ]
    }
  ]
}
```

Data classes
```kotlin
data class Period(val startDate: LocalDateTime, val endDate: LocalDateTime?)
data class User(val name: String, val role: String?, val phones: List<String>)
data class A(val id: String, val period: Period, val users: List<User>)
```

## JsPath
JsPath represents the location of data in a JsValue structure.

Simple path
```kotlin
val startDatePath = "period" / "startDate"
```
Indexed path
```kotlin
val roleFirstUserPath = "users" / 0 / "role"
```


## JsReader
Example 1
```kotlin
val periodReader: JsReader<User> = run {
    val nameAttributeReader = readRequired(byName = "name", using = BasePrimitiveReader.string)
    val roleAttributeReader = readNullable(byName = "role", using = BasePrimitiveReader.string)
    val phonesAttributeReader = readTraversable(byName = "phones", using = BasePrimitiveReader.string)

    reader { input ->
        val name = read(from = input, using = nameAttributeReader).onFailure { return@reader it }
        val role = read(from = input, using = roleAttributeReader).onFailure { return@reader it }
        val phones = read(from = input, using = phonesAttributeReader).onFailure { return@reader it }
        JsResult.Success(User(name = name, role = role, phones = phones))
    }
}
```
Example 2
```kotlin
val periodReader: JsReader<User> = run {
    val nameAttributeReader = readRequired(byName = "name", using = BasePrimitiveReader.string)
    val roleAttributeReader = readNullable(byName = "role", using = BasePrimitiveReader.string)
    val phonesAttributeReader = readTraversable(byName = "phones", using = BasePrimitiveReader.string)

    reader { input ->
        JsResult.fx {
            val (name) = read(from = input, using = nameAttributeReader)
            val (role) = read(from = input, using = roleAttributeReader)
            val (phones) = read(from = input, using = phonesAttributeReader)
            User(name = name, role = role, phones = phones)
        }
    }
}
```

## JsWriter
```kotlin
val userWriter = objectWriter<User> {
    write(from = User::name, to = "name", using = BasePrimitiveWriter.string)
    writeOptional(from = User::role, to = "role", using = BasePrimitiveWriter.string)
    writeTraversable(from = User::phones, to = "phones", using = BasePrimitiveWriter.string)
}
...
val user = User(...)
val output: JsValue = userWriter.write(user)
val json = output.toString()
```