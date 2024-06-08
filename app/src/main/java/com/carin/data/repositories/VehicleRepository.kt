package com.carin.data.repositories

import com.carin.data.local.daos.VehicleDao
import com.carin.data.mappers.toVehicleEntity
import com.carin.data.mappers.toVehicleModel
import com.carin.data.remote.VehicleService
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.VehicleModel
import com.carin.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.util.Date
import kotlin.time.Duration.Companion.minutes

class VehicleRepository(
    private val vehicleDao: VehicleDao,
    private val vehicleService: VehicleService
) {

    suspend fun getVehiclesList(
        search: String?,
        status: VehicleStatus?,
        page: Int = 1,
        perPage: Int = 50
    ): Flow<Resource<List<VehicleModel>>> {
        return flow {
            emit(Resource.Loading())

            val localVehicles = vehicleDao.getListOfVehicles(search, status, page, perPage)
            val isToFetchRemote = localVehicles.isEmpty()
                    || localVehicles.size < perPage
                    || localVehicles.first().localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
            if (localVehicles.isNotEmpty()) {
                emit(
                    Resource.Success(
                        data = localVehicles.map { it.toVehicleModel() }
                    )
                )
            }

            if (isToFetchRemote) {
                emit(Resource.Loading())
                val remoteVehicles = try {
                    val response = vehicleService.getVehicles(search, status?.description, page, perPage).execute()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        null
                    }
                } catch(e: IOException) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    null
                } catch (e: HttpException) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    null
                }

                remoteVehicles?.let { vehicleDtoList ->
                    val vehicleEntities = vehicleDtoList.map { it.toVehicleEntity() }
                    vehicleDao.upsertVehicles(vehicleEntities)

                    emit(
                        Resource.Success(
                            data =  vehicleDao.getListOfVehicles(search, status, page, perPage).map { it.toVehicleModel() }
                        )
                    )
                }

                if (remoteVehicles.isNullOrEmpty() && localVehicles.isEmpty()) {
                    emit(Resource.Success(data = emptyList()))
                }

            }
        }.flowOn(Dispatchers.IO)
    }
}