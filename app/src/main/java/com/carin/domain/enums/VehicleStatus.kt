package com.carin.domain.enums

enum class VehicleStatus(
    val statusId: Int,
    val description: String,
    val stringKey: String,
    val externalKey: String

) {
    None(0, "None", "none", "none"),
    InUse(1, "InUse", "in_use", "inUse"),
    Repairing(2, "Repairing", "Repairing", "repairing");

    companion object {
        private val mapById = entries.associateBy(VehicleStatus::statusId)
        private val mapByDescription = entries.associateBy { it.description.lowercase() }

        fun fromId(statusId: Int): VehicleStatus? = mapById[statusId]
        fun fromDescription(description: String): VehicleStatus? = mapByDescription[description.lowercase()]
    }
}
