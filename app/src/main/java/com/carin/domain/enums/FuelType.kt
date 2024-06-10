package com.carin.domain.enums

enum class FuelType(val fuelTypeId: Int, val description: String, val stringKey: String) {
    Diesel(1, "Diesel", "diesel"),
    Petrol(2, "Petrol", "petrol"),
    Electric(3, "Electric", "electric");

    companion object {
        private val mapById = entries.associateBy(FuelType::fuelTypeId)
        private val mapByDescription = entries.associateBy(FuelType::description)

        fun fromId(fuelTypeId: Int): FuelType? = mapById[fuelTypeId]
        fun fromDescription(description: String): FuelType? = mapByDescription[description]
    }
}