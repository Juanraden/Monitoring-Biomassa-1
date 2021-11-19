package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import kotlinx.coroutines.flow.Flow

@Dao
interface BiotaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listBiota: List<Biota>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(biota: Biota)

    @Query("SELECT * FROM biota WHERE tanggal_panen = 0 AND kerambaid =:id")
    fun getAll(id: Int): Flow<List<Biota>>

    @Query("SELECT * FROM biota WHERE tanggal_panen > 0 AND kerambaid =:id")
    fun getAllHistory(id: Int): Flow<List<Biota>>

    @Query("SELECT * FROM biota WHERE biotaid =:id")
    fun getById(id: Int): Flow<Biota>
}