package com.carin.domain.enums

enum class VehicleStatus(
    val statusId: Int,
    val description: String
) {
    None(0, "none"),
    Permanent(1, "permanent"),
    InUse(2, "inUse"),
    Repairing(3, "repairing"),
    ;

    companion object {
        private val mapById = entries.associateBy(VehicleStatus::statusId)
        private val mapByDescription = entries.associateBy { it.description.lowercase() }

        fun fromId(statusId: Int): VehicleStatus? = mapById[statusId]
        fun fromDescription(description: String): VehicleStatus? = mapByDescription[description.lowercase()]
    }
}
