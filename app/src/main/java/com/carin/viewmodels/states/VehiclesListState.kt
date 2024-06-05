package com.carin.viewmodels.states

import com.carin.domain.enums.VehicleType
import com.carin.domain.models.VehicleModel

sealed class VehiclesListState {
    data class Loading(val vehicleType: VehicleType) : VehiclesListState()
    data class Success(
        val vehicles: List<VehicleModel>,
        val vehicleType: VehicleType,
        val isEmpty: Boolean = false,
        val isAppending: Boolean = false
    ) : VehiclesListState()
    data class Error(
        val vehicleType: VehicleType,
        val message: String
    ) : VehiclesListState()
}
