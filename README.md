<img src="logo.png" alt="Airflux logo" />

# The Airflux serialization library.

![CI](https://github.com/airflux/airflux/workflows/CI/badge.svg)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=alert_status)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=bugs)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=coverage)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Codecov](https://codecov.io/gh/airflux/airflux/branch/main/graph/badge.svg?token=QBD7092MJI)](https://codecov.io/gh/airflux/airflux)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=airflux_airflux&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=airflux_airflux)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/53e1a68ffc064a6e8d9a01a4c3027764)](https://www.codacy.com/gh/airflux/airflux/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=airflux/airflux&amp;utm_campaign=Badge_Grade)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/5511/badge)](https://bestpractices.coreinfrastructure.org/projects/5511)

[![](https://jitpack.io/v/airflux/airflux.svg)](https://jitpack.io/#airflux/airflux)

The library to parse, validate and generate data in the JSON (JavaScript Object Notation) format.

The documentation site: [https://airflux.github.io/airflux-serialization/](https://airflux.github.io/airflux-serialization/)

# Installation

```
repositories {
    ...
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.airflux.airflux:airflux-serialization:v0.0.1-alpha.1")
    
    implementation("com.github.airflux.airflux:airflux-jackson-parser:v0.0.1-alpha.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3") {
        exclude(group = "org.jetbrains.kotlin")
    }
}
```

# Codecov graphs

[![Codecov Sunburst](https://codecov.io/gh/airflux/airflux-serialization/branch/main/graphs/sunburst.svg?token=QBD7092MJI)](https://codecov.io/gh/airflux/airflux-serialization/branch/main/graphs/sunburst.svg?token=QBD7092MJI)

# License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fairflux%2Fairflux.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fairflux%2Fairflux?ref=badge_large)
