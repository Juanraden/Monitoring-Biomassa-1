package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import kotlinx.coroutines.flow.Flow

@Dao
interface KerambaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(listKeramba: List<Keramba>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(keramba: Keramba)

    @Update
    fun updateOne(keramba: Keramba)

    @Query("SELECT * FROM keramba ORDER BY tanggal_install")
    fun getAll(): Flow<List<Keramba>>

    @Query("SELECT * FROM keramba WHERE kerambaid = :id")
    fun getById(id: Int): Flow<Keramba>
}