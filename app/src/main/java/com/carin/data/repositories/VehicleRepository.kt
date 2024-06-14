package com.carin.data.repositories

import com.carin.data.local.daos.VehicleDao
import com.carin.data.mappers.toVehicleCreationRequest
import com.carin.data.mappers.toVehicleEntity
import com.carin.data.mappers.toVehicleModel
import com.carin.data.mappers.toVehicleUpdateRequest
import com.carin.data.remote.VehicleService
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.VehicleCreationModel
import com.carin.domain.models.VehicleModel
import com.carin.domain.models.VehicleUpdateModel
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
                        response.body()?.data
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = errorBody?.let {
                            try {
                                org.json.JSONObject(it).getString("message")
                            } catch (e: Exception) {
                                response.message()
                            }
                        } ?: response.message()
                        emit(Resource.Error(errorMessage))
                        null
                    }
                } catch (e: IOException) {
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

    suspend fun getVehicleById(id: String, forceRefresh: Boolean = false): Flow<Resource<VehicleModel>> {
        return flow {
            emit(Resource.Loading())

            val localVehicle = vehicleDao.getVehicleById(id)
            val isToFetchRemote = localVehicle == null || localVehicle.localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
            if (localVehicle != null) {
                emit(Resource.Success(data = localVehicle.toVehicleModel()))
            }

            if (isToFetchRemote || forceRefresh) {
                emit(Resource.Loading())

                val remoteVehicle = try {
                    val response = vehicleService.getVehicleById(id).execute()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = errorBody?.let {
                            try {
                                org.json.JSONObject(it).getString("message")
                            } catch (e: Exception) {
                                response.message()
                            }
                        } ?: response.message()
                        emit(Resource.Error(errorMessage))
                        null
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    null
                } catch (e: HttpException) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    null
                }

                remoteVehicle?.let { vehicleDto ->
                    val vehicleEntity = vehicleDto.toVehicleEntity()
                    vehicleDao.upsertVehicle(vehicleEntity)

                    val upsertedVehicle = vehicleDao.getVehicleById(id)
                    if (upsertedVehicle != null) {
                        emit(Resource.Success(data = upsertedVehicle.toVehicleModel()))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createVehicle(vehicleCreationModel: VehicleCreationModel): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())

            val remoteVehicle = try {
                val response = vehicleService.createVehicle(vehicleCreationModel.toVehicleCreationRequest()).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let {
                        try {
                            org.json.JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            response.message()
                        }
                    } ?: response.message()
                    emit(Resource.Error(errorMessage))
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't create vehicle"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't create vehicle"))
                null
            }

            remoteVehicle?.let { vehicleDto ->
                val vehicleEntity = vehicleDto.toVehicleEntity()
                vehicleDao.insertVehicle(vehicleEntity)

                emit(Resource.Success(remoteVehicle.id))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateVehicle(vehicleUpdateModel: VehicleUpdateModel): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            val remoteVehicle = try {
                val response =
                    vehicleService.updateVehicle(vehicleUpdateModel.id, vehicleUpdateModel.toVehicleUpdateRequest()).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let {
                        try {
                            org.json.JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            response.message()
                        }
                    } ?: response.message()
                    emit(Resource.Error(errorMessage))
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("ERROR"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("ERROR"))
                null
            }

            if (remoteVehicle != null) {
                vehicleDao.updatePartialVehicle(
                    vehicleUpdateModel.id,
                    vehicleUpdateModel.color,
                    vehicleUpdateModel.kms,
                    vehicleUpdateModel.averageFuelConsumption,
                    vehicleUpdateModel.status
                )

                emit(Resource.Success(true))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteVehicle(id: String): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            val isDeleted = try {
                val response = vehicleService.deleteVehicle(id).execute()
                if (response.isSuccessful) {
                    true
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let {
                        try {
                            org.json.JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            response.message()
                        }
                    } ?: response.message()
                    emit(Resource.Error(errorMessage))
                    false
                }
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't delete vehicle"))
                false
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't delete vehicle"))
                false
            }

            if (isDeleted) {
                vehicleDao.deleteVehicle(id)
                emit(Resource.Success(true))
            }
        }.flowOn(Dispatchers.IO)
    }
}