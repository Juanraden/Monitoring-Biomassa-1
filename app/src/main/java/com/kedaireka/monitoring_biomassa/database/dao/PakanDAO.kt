package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import kotlinx.coroutines.flow.Flow

@Dao
interface PakanDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(pakan: Pakan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(listPakan: List<Pakan>)

    @Query("SELECT * FROm pakan")
    fun getAll(): Flow<List<Pakan>>

    @Query("DELETE FROM pakan")
    suspend fun deleteAllPakan()

    @Query("SELECT COUNT(*) FROM pakan")
    fun getPakanCount(): Int
}