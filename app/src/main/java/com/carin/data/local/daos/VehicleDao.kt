package com.carin.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.carin.data.local.entities.VehicleEntity
import com.carin.domain.enums.VehicleStatus


@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    suspend fun getVehicleById(id: String): VehicleEntity?

    @Insert
    suspend fun insertVehicle(vehicles: VehicleEntity)

    @Upsert
    suspend fun upsertVehicle(vehicle: VehicleEntity)

    @Upsert
    suspend fun upsertVehicles(vehicles: List<VehicleEntity>)

    @Query("""
        UPDATE vehicles SET
        color = CASE WHEN :color IS NOT NULL THEN :color ELSE color END,
        kms = CASE WHEN :kms IS NOT NULL THEN :kms ELSE Kms END,
        averageFuelConsumption = CASE WHEN :averageFuelConsumption IS NOT NULL THEN :averageFuelConsumption ELSE averageFuelConsumption END,
        status = CASE WHEN :status IS NOT NULL THEN :status ELSE status END
        WHERE id = :id
    """)
    suspend fun updatePartialVehicle(
        id: String,
        color: String?,
        kms: Double?,
        averageFuelConsumption: Double?,
        status: VehicleStatus?
    )

    @Query("UPDATE vehicles SET isDeleted = 1 WHERE id = :id")
    suspend fun deleteVehicle(id: String)

    @RawQuery
    suspend fun getVehicles(query: SupportSQLiteQuery): List<VehicleEntity>

    suspend fun getListOfVehicles(search: String?, status: VehicleStatus?, page: Int = 1, perPage: Int = 10): List<VehicleEntity> {
        val queryBuilder = StringBuilder("SELECT * FROM vehicles WHERE isDeleted = 0")
        val args = mutableListOf<Any?>()

        if (!search.isNullOrEmpty()) {
            queryBuilder.append(" AND (licensePlate LIKE ? OR model LIKE ? OR brand LIKE ? OR vin LIKE ? OR color LIKE ? OR category LIKE ?)")
            val searchPattern = "%${search}%"
            args.add(searchPattern)
            args.add(searchPattern)
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
        return getVehicles(query)
    }
}
