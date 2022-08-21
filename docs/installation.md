# Installation

## Gradle

Added the repository

```kotlin
repositories {
    ...
    maven {
        url = uri("https://jitpack.io")
    }
}
```

Added the serialization library

```kotlin
dependencies {
    implementation("com.github.airflux.airflux:airflux-serialization:version")
}
```

Added the parser for JSON

```kotlin
dependencies {
    implementation("com.github.airflux.airflux:airflux-jackson-parser:version")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3") {
        exclude(group = "org.jetbrains.kotlin")
    }
}
```

## Maven

Added the repository

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Added the serialization library

```xml
<dependency>
    <groupId>com.github.airflux.airflux</groupId>
    <artifactId>airflux-serialization</artifactId>
    <version>version</version>
</dependency>
```

Added the parser for JSON

```xml
<dependency>
    <groupId>com.github.airflux.airflux</groupId>
    <artifactId>airflux-jackson-parser</artifactId>
    <version>version</version>
</dependency>
<dependency>
    <groupId>com.github.airflux.airflux</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.13.3</version>
</dependency>
<dependency>
    <groupId>com.github.airflux.airflux</groupId>
    <artifactId>jackson-module-kotlin</artifactId>
    <version>2.13.3</version>
</dependency>
```
