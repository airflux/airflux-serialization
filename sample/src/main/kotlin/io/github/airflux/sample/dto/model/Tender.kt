package io.github.airflux.sample.dto.model

data class Tender(
    val id: String,
    val title: String,
    val value: Value?,
    val lots: List<Lot>
)
