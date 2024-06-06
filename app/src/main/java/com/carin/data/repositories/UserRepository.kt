package com.carin.data.repositories

import com.carin.data.local.daos.UserDao
import com.carin.data.mappers.toAuthRegisterRequest
import com.carin.data.mappers.toUserEntity
import com.carin.data.mappers.toUserModel
import com.carin.data.remote.AuthService
import com.carin.data.remote.UserService
import com.carin.domain.enums.Role
import com.carin.domain.models.UserModel
import com.carin.domain.models.UserRegisterModel
import com.carin.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.util.Date
import kotlin.time.Duration.Companion.minutes

class UserRepository(
    private val userDao: UserDao,
    private val userService: UserService,
    private val authService: AuthService
) {

    suspend fun getUsersList(
        search: String?,
        role: Role?,
        page: Int = 1,
        perPage: Int = 50
    ): Flow<Resource<List<UserModel>>> {
        return flow {
            emit(Resource.Loading())

            val localUsers = userDao.getListOfUsers(search, role, page, perPage)
            val isToFetchRemote = localUsers.isEmpty()
                    || localUsers.size < perPage
                    || localUsers.first().localLastUpdateDateUtc < Date(System.currentTimeMillis() - 15.minutes.inWholeMilliseconds)
            if (localUsers.isNotEmpty()) {
                emit(
                    Resource.Success(
                        data = localUsers.map { it.toUserModel() },
                        waitForRemote = isToFetchRemote
                    )
                )
            }

            if (isToFetchRemote) {
                emit(Resource.Loading())
                val remoteUsers = try {
                    val response = userService.getUsers(search, role, page, perPage).execute()
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        emit(Resource.Error(response.message()))
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

                remoteUsers?.let { userDtoList ->
                    val userEntities = userDtoList.map { it.toUserEntity() }
                    userDao.upsertUsers(userEntities)

                    emit(
                        Resource.Success(
                            data =  userDao.getListOfUsers(search, role, page, perPage).map { it.toUserModel() }
                        )
                    )
                }

                if (remoteUsers.isNullOrEmpty() && localUsers.isEmpty()) {
                    emit(Resource.Success(data = emptyList()))
                }

            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun registerUser(userRegisterModel: UserRegisterModel): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            val remoteUser = try {
                val response = authService.register(userRegisterModel.toAuthRegisterRequest()).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    emit(Resource.Error("Couldn't register user: " + response.message()))
                    null
                }
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't register user"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't register user"))
                null
            }

            remoteUser?.let { authRegisterDto ->
                val userEntity = authRegisterDto.toUserEntity()
                userDao.insertUser(userEntity)

                emit(Resource.Success(true))
            }
        }.flowOn(Dispatchers.IO)
    }
}