package com.carin.data.repositories

import com.carin.data.local.daos.RouteDao
import com.carin.data.local.daos.UserDao
import com.carin.data.local.daos.VehicleDao
import com.carin.data.mappers.toRouteEntity
import com.carin.data.mappers.toRouteModel
import com.carin.data.mappers.toUserEntity
import com.carin.data.mappers.toVehicleEntity
import com.carin.data.remote.RouteService
import com.carin.data.remote.UserService
import com.carin.data.remote.VehicleService
import com.carin.domain.enums.RouteStatus
import com.carin.domain.models.RouteModel
import com.carin.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.util.Date
import kotlin.time.Duration.Companion.minutes

class RouteRepository(
    private val routeDao: RouteDao,
    private val userDao: UserDao,
    private val vehicleDao: VehicleDao,
    private val routeService: RouteService,
    private val userService: UserService,
    private val vehicleService: VehicleService
) {

    suspend fun getRoutesList(
        search: String?,
        status: RouteStatus?,
        page: Int = 1,
        perPage: Int = 30,
        userId: Int? = null,
        vehicleId: String? = null,
        forceRefresh: Boolean = false
    ): Flow<Resource<List<RouteModel>>> {
        return flow {
            emit(Resource.Loading())

            val localRoutes = routeDao.getListOfRoutes(search, status, page, perPage, userId, vehicleId)
            val isToFetchRemote = localRoutes.isEmpty()
                    || localRoutes.size < perPage
                    || localRoutes.first().route.localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
            if (localRoutes.isNotEmpty()) {
                emit(
                    Resource.Success(
                        data = localRoutes.map { it.toRouteModel() }
                    )
                )
            }

            if (isToFetchRemote || forceRefresh) {
                emit(Resource.Loading())
                val remoteRoutes = try {
                    val response = routeService.getRoutes(
                        search,
                        status.toString().lowercase(),
                        page,
                        perPage,
                        userId,
                        vehicleId
                    ).execute()
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

                if (!remoteRoutes.isNullOrEmpty()) {
                    // Fetch the users for each route
                    val userIdsToUpsert = mutableSetOf<Int>()
                    val userEntities = remoteRoutes.mapNotNull { routeDto ->
                        try {
                            val localUser = userDao.getUserById(routeDto.userId)

                            val isUserUpdated = localUser != null
                                    && localUser.localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
                            if (isUserUpdated) {
                                localUser
                            } else {
                                val userResponse = userService.getUserById(routeDto.userId).execute()
                                if (userResponse.isSuccessful) {
                                    userIdsToUpsert.add(routeDto.userId)
                                    userResponse.body()?.toUserEntity()
                                } else {
                                    null
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            null
                        } catch (e: HttpException) {
                            e.printStackTrace()
                            null
                        }
                    }.takeIf { it.isNotEmpty() } ?: emptyList()

                    // Upsert users into the database
                    if (userEntities.isNotEmpty()) {
                        userDao.upsertUsers(userEntities.filter { it.id in userIdsToUpsert })
                    }

                    val vehicleIdsToUpsert = mutableSetOf<String>()
                    val vehicleEntities = remoteRoutes.mapNotNull { routeDto ->
                        try {
                            val localVehicle = vehicleDao.getVehicleById(routeDto.vehicleId)

                            val isUserUpdated = localVehicle != null
                                    && localVehicle.localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
                            if (isUserUpdated) {
                                localVehicle
                            } else {
                                val vehicleResponse = vehicleService.getVehicleById(routeDto.vehicleId).execute()
                                if (vehicleResponse.isSuccessful) {
                                    vehicleIdsToUpsert.add(routeDto.vehicleId)
                                    vehicleResponse.body()?.toVehicleEntity()
                                } else {
                                    null
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            null
                        } catch (e: HttpException) {
                            e.printStackTrace()
                            null
                        }
                    }.takeIf { it.isNotEmpty() } ?: emptyList()

                    // Upsert vehicles into the database
                    if (vehicleEntities.isNotEmpty()) {
                        vehicleDao.upsertVehicles(vehicleEntities.filter { it.id in vehicleIdsToUpsert })
                    }

                    // Upsert routes into the database
                    val routeEntities = remoteRoutes.map { it.toRouteEntity() }.takeIf { it.isNotEmpty() } ?: emptyList()
                    if (routeEntities.isNotEmpty()) {
                        routeDao.upsertRoutes(routeEntities)
                    }

                    emit(
                        Resource.Success(
                            data = routeDao.getListOfRoutes(search, status, page, perPage, userId, vehicleId).map { it.toRouteModel() }
                        )
                    )
                } else {
                    if (localRoutes.isEmpty()) {
                        emit(Resource.Success(data = emptyList()))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}