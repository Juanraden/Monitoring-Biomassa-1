package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.kedaireka.monitoring_biomassa.database.entity.Biota

@Dao
interface BiotaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listBiota: List<Biota>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(biota: Biota)
}