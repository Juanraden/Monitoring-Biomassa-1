package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.database.relation.KerambaAndBiota
import kotlinx.coroutines.flow.Flow

@Dao
interface KerambaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listKeramba: List<Keramba>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(keramba: Keramba)

    @Update
    fun updateOne(keramba: Keramba)

    @Query("SELECT * FROM keramba ORDER BY tanggal_install")
    fun getAll(): Flow<List<Keramba>>

    @Query("SELECT * FROM keramba WHERE keramba_id = :id")
    fun getById(id: Int): Flow<Keramba>

    @Transaction
    @Query("SELECT * FROM keramba")
    fun getKerambaAndBiota(): Flow<List<KerambaAndBiota>>

    @Query("DELETE FROM keramba")
    suspend fun deleteAllKeramba()

    @Query("SELECT COUNT(*) FROM keramba")
    fun getKerambaCount(): Int

    @Query("DELETE FROM keramba WHERE keramba_id =:kerambaId")
    suspend fun deleteOne(kerambaId: Int)
}