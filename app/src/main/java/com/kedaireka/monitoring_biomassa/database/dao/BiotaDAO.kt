package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import kotlinx.coroutines.flow.Flow

@Dao
interface BiotaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listBiota: List<Biota>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(biota: Biota)

    @Update
    suspend fun updateOne(biota: Biota)

    @Query("SELECT * FROM biota WHERE tanggal_panen = 0 AND keramba_id =:id")
    fun getAll(id: Int): Flow<List<Biota>>

    @Query("SELECT * FROM biota WHERE tanggal_panen > 0 AND keramba_id =:id")
    fun getAllHistory(id: Int): Flow<List<Biota>>

    @Query("SELECT * FROM biota WHERE biota_id =:id")
    fun getById(id: Int): Flow<Biota>

    @Query("SELECT COUNT(*) FROM biota WHERE tanggal_panen = 0 AND keramba_id =:kerambaId")
    fun getBiotaCountFromKeramba(kerambaId: Int): Int

    @Query("DELETE FROM biota WHERE keramba_id =:kerambaId")
    fun deleteBiotaFromKeramba(kerambaId: Int)
}