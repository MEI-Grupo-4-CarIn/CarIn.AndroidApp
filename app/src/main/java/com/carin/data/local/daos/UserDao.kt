package com.carin.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
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

    @Insert
    suspend fun insertUser(user: UserEntity)

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Upsert
    suspend fun upsertUsers(users: List<UserEntity>)

    @Query("""
        UPDATE users SET
        firstName = CASE WHEN :firstName IS NOT NULL THEN :firstName ELSE firstName END,
        lastName = CASE WHEN :lastName IS NOT NULL THEN :lastName ELSE lastName END,
        email = CASE WHEN :email IS NOT NULL THEN :email ELSE email END
        WHERE id = :id
    """)
    suspend fun updatePartialUser(
        id: Int,
        firstName: String?,
        lastName: String?,
        email: String?,
    )

    @Query("""
        UPDATE users SET
        status = 1,
        roleId = CASE WHEN :role IS NOT NULL THEN :role ELSE roleId END
        WHERE id = :id
    """)
    suspend fun approveUser(id: Int, role: Role?)

    @Query("UPDATE users SET status = 0 WHERE id = :id")
    suspend fun deleteUser(id: Int)

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

    suspend fun getWaitingForApprovalUsers(page: Int = 1, perPage: Int = 10): List<UserEntity> {
        val queryBuilder = StringBuilder("SELECT * FROM users WHERE status = 0")
        val args = mutableListOf<Any?>()

        queryBuilder.append(" ORDER BY id DESC")

        val offset = (page - 1) * perPage
        queryBuilder.append(" LIMIT ? OFFSET ?")
        args.add(perPage)
        args.add(offset)

        val query = SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
        return getUsers(query)
    }
}
