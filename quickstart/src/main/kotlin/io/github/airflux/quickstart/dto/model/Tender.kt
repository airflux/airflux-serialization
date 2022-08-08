package io.github.airflux.quickstart.dto.model

data class Tender(
    val id: String,
    val title: String?,
    val value: Value?,
    val lots: Lots
)
