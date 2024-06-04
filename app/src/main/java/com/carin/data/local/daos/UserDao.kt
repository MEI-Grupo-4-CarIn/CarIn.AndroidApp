package com.carin.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.carin.data.local.entities.UserEntity
import com.carin.domain.enums.Role

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Upsert
    suspend fun upsertUsers(users: List<UserEntity>)

    @RawQuery
    suspend fun getUsers(query: SupportSQLiteQuery): List<UserEntity>

    suspend fun getListOfUsers(search: String?, role: Role?, page: Int = 1, perPage: Int = 10): List<UserEntity> {
        val queryBuilder = StringBuilder("SELECT * FROM users WHERE status = 1")
        val args = mutableListOf<Any?>()

        if (!search.isNullOrEmpty()) {
            queryBuilder.append(" AND (firstName LIKE ? OR lastName LIKE ? OR email LIKE ?)")
            val searchPattern = "%${search}%"
            args.add(searchPattern)
            args.add(searchPattern)
            args.add(searchPattern)
        }

        if (role != null) {
            queryBuilder.append(" AND roleId = ?")
            args.add(role.roleId)
        }

        queryBuilder.append(" ORDER BY id DESC")

        val offset = (page - 1) * perPage
        queryBuilder.append(" LIMIT ? OFFSET ?")
        args.add(perPage)
        args.add(offset)

        val query = SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
        return getUsers(query)
    }
}
