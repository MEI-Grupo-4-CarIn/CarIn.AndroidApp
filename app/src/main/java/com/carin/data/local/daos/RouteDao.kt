package com.carin.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.carin.data.local.entities.RouteEntity
import com.carin.data.local.entities.RouteWithInfoEntity
import com.carin.domain.enums.Role
import com.carin.domain.enums.RouteStatus
import com.carin.domain.models.UserAuth
import java.util.Date

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes WHERE id = :id LIMIT 1")
    suspend fun getRouteById(id: String): RouteWithInfoEntity?

    @Insert
    suspend fun insertRoute(routes: RouteEntity)

    @Upsert
    suspend fun upsertRoute(routes: RouteEntity)

    @Upsert
    suspend fun upsertRoutes(routes: List<RouteEntity>)

    @Query("""
        UPDATE routes SET
        userId = CASE WHEN :userId IS NOT NULL THEN :userId ELSE userId END,
        vehicleId = CASE WHEN :vehicleId IS NOT NULL THEN :vehicleId ELSE vehicleId END,
        startDate = CASE WHEN :startDate IS NOT NULL THEN :startDate ELSE startDate END,
        status = CASE WHEN :status IS NOT NULL THEN :status ELSE status END,
        avoidTolls = CASE WHEN :avoidTolls IS NOT NULL THEN :avoidTolls ELSE avoidTolls END,
        avoidHighways = CASE WHEN :avoidHighways IS NOT NULL THEN :avoidHighways ELSE avoidHighways END
        WHERE id = :id
    """)
    suspend fun updatePartialRoute(
        id: String,
        userId: Int?,
        vehicleId: String?,
        startDate: Date?,
        status: RouteStatus?,
        avoidTolls: Boolean?,
        avoidHighways: Boolean?
    )

    @Query("UPDATE routes SET isDeleted = 1 WHERE id = :id")
    suspend fun deleteRoute(id: String)

    @RawQuery
    suspend fun getRoutes(query: SupportSQLiteQuery): List<RouteWithInfoEntity>

    suspend fun getListOfRoutes(
        search: String?,
        status: RouteStatus?,
        page: Int = 1,
        perPage: Int = 10,
        userId: Int?,
        vehicleId: String?,
        userAuth: UserAuth?
    ): List<RouteWithInfoEntity> {
        val queryBuilder = StringBuilder("SELECT * FROM routes WHERE isDeleted = 0")
        val args = mutableListOf<Any?>()

        if (!search.isNullOrEmpty()) {
            queryBuilder.append(" AND (startPoint_city LIKE ? OR startPoint_country LIKE ? OR endPoint_city LIKE ? OR endPoint_country LIKE ?)")
            val searchPattern = "%${search}%"
            args.add(searchPattern)
            args.add(searchPattern)
            args.add(searchPattern)
            args.add(searchPattern)
        }

        if (status != null) {
            queryBuilder.append(" AND status = ?")
            args.add(status.statusId)
        }

        if (userId != null) {
            queryBuilder.append(" AND userId = ?")
            args.add(userId)
        }

        if (!vehicleId.isNullOrEmpty()) {
            queryBuilder.append(" AND vehicleId = ?")
            args.add(vehicleId)
        }

        if (userAuth?.role == Role.Driver) {
            queryBuilder.append(" AND userId = ?")
            args.add(userAuth.userId)
        }

        queryBuilder.append(" ORDER BY creationDateUtc DESC")

        val offset = (page - 1) * perPage
        queryBuilder.append(" LIMIT ? OFFSET ?")
        args.add(perPage)
        args.add(offset)

        val query = SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
        return getRoutes(query)
    }
}