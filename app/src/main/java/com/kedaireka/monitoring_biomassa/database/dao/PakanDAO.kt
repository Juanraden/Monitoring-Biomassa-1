package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import kotlinx.coroutines.flow.Flow

@Dao
interface PakanDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(pakan: Pakan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listPakan: List<Pakan>)

    @Update
    suspend fun updateOne(pakan: Pakan)

    @Query("SELECT * FROm pakan")
    fun getAll(): Flow<List<Pakan>>

    @Query("SELECT * FROM pakan WHERE pakan_id = :id")
    fun getById(id: Int): Flow<Pakan>

    @Query("DELETE FROM pakan")
    suspend fun deleteAllPakan()
}