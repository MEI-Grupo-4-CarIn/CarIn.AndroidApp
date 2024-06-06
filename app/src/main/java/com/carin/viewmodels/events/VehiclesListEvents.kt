package com.carin.viewmodels.events

import com.carin.domain.enums.VehicleType

sealed class VehiclesListEvent {
    data class UpdateSearch(val searchQuery: String?) : VehiclesListEvent()
    data class LoadVehicles(val vehicleType: VehicleType, val page: Int = 1) : VehiclesListEvent()
    data class LoadMoreVehicles(val vehicleType: VehicleType) : VehiclesListEvent()
}