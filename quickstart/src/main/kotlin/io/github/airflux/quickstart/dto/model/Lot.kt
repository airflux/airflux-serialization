package io.github.airflux.quickstart.dto.model

data class Lot(
    val id: String,
    val status: LotStatus,
    val value: Value
)
