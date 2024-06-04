package com.carin.data.repositories

import com.carin.data.local.daos.RouteDao
import com.carin.data.local.daos.UserDao
import com.carin.data.mappers.toRouteEntity
import com.carin.data.mappers.toRouteModel
import com.carin.data.mappers.toUserEntity
import com.carin.data.remote.RouteService
import com.carin.data.remote.UserService
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
    private val routeService: RouteService,
    private val userService: UserService
) {

    suspend fun getRoutesList(
        search: String?,
        status: RouteStatus?,
        page: Int = 1,
        perPage: Int = 10
    ): Flow<Resource<List<RouteModel>>> {
        return flow {
            emit(Resource.Loading())

            val localRoutes = routeDao.getListOfRoutes(search, status, page, perPage)
            val isToFetchRemote = localRoutes.isEmpty()
                    || localRoutes.size < perPage
                    || localRoutes.first().route.localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
            if (localRoutes.isNotEmpty()) {
                emit(
                    Resource.Success(
                        data = localRoutes.map { it.toRouteModel() },
                        waitForRemote = isToFetchRemote
                    )
                )
            }

            if (isToFetchRemote) {
                val remoteRoutes = try {
                    val response = routeService.getRoutes(search, status.toString().lowercase(), page, perPage).execute()
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

                remoteRoutes?.let { routeDtoList ->
                    // Fetch the users for each route
                    val userIdsToUpsert = mutableSetOf<Int>()
                    val userEntities = routeDtoList.mapNotNull { routeDto ->
                        try {
                            val localUser = userDao.getUserById(routeDto.userId)

                            val isUserUpdated = localUser != null && localUser.localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
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
                    }
                    val routeEntities = routeDtoList.map { it.toRouteEntity() }

                    // Insert/Update routes and users into the database
                    userDao.upsertUsers(userEntities.filter { it.id in userIdsToUpsert })
                    routeDao.upsertRoutes(routeEntities)

                    emit(
                        Resource.Success(
                            data = routeDao.getListOfRoutes(search, status, page, perPage).map { it.toRouteModel() }
                        ))
                }

                if (remoteRoutes.isNullOrEmpty() && localRoutes.isEmpty()) {
                    emit(Resource.Success(data = emptyList()))
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}