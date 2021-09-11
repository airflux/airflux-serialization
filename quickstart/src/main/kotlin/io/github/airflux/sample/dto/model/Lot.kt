package io.github.airflux.sample.dto.model

data class Lot(
    val id: String,
    val status: LotStatus,
    val value: Value
)
