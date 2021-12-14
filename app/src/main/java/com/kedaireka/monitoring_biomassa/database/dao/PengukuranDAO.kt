package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kedaireka.monitoring_biomassa.database.entity.Pengukuran
import kotlinx.coroutines.flow.Flow

@Dao
interface PengukuranDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(pengukuran: Pengukuran)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(listPengukuran: List<Pengukuran>)

    @Query("SELECT * FROM pengukuran WHERE biota_id =:biotaId ORDER BY tanggal_ukur DESC LIMIT 10")
    fun getAll(biotaId: Int): Flow<List<Pengukuran>>

    @Query("SELECT COUNT(*) FROM pengukuran WHERE biota_id =:biotaId ORDER BY tanggal_ukur")
    fun getPengukuranCountFromBiota(biotaId: Int): Int

    @Query("DELETE FROM pengukuran WHERE biota_id =:biotaId")
    fun deletePengukuranFromBiota(biotaId: Int)
}