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

    @Query("SELECT * FROM pengukuran WHERE biotaid =:biotaid")
    fun getAll(biotaid: Int): Flow<List<Pengukuran>>
}