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

    @Query("SELECT * FROM pengukuran WHERE biota_id =:biota_id ORDER BY tanggal_ukur DESC LIMIT 10")
    fun getAll(biota_id: Int): Flow<List<Pengukuran>>
}