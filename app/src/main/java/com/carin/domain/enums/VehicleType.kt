package com.carin.domain.enums
enum class VehicleType(val labelPt: String, val labelEn: String) {
    ALL("Todos", "All"),
    IN_USE("Em uso", "In use"),
    REPAIRING("Em reparação", "Repairing"),
    NONE("Nenhum", "None");

    companion object {
        fun toVehicleStatus(vehicleType: VehicleType) : VehicleStatus? {
            return when (vehicleType) {
                ALL -> null
                IN_USE -> VehicleStatus.InUse
                REPAIRING -> VehicleStatus.Repairing
                NONE -> VehicleStatus.None
            }
        }
    }
}