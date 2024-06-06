package com.carin.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.carin.data.local.entities.RouteEntity
import com.carin.data.local.entities.RouteWithInfoEntity
import com.carin.domain.enums.RouteStatus

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes WHERE id = :id LIMIT 1")
    suspend fun getRouteById(id: String): RouteWithInfoEntity?

    @Upsert
    suspend fun upsertRoutes(routes: List<RouteEntity>)

    @RawQuery
    suspend fun getRoutes(query: SupportSQLiteQuery): List<RouteWithInfoEntity>

    suspend fun getListOfRoutes(search: String?, status: RouteStatus?, page: Int = 1, perPage: Int = 10): List<RouteWithInfoEntity> {
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

        queryBuilder.append(" ORDER BY creationDateUtc DESC")

        val offset = (page - 1) * perPage
        queryBuilder.append(" LIMIT ? OFFSET ?")
        args.add(perPage)
        args.add(offset)

        val query = SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
        return getRoutes(query)
    }
}