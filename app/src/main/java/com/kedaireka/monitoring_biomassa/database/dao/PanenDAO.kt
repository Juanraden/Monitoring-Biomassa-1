package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kedaireka.monitoring_biomassa.database.entity.Panen
import kotlinx.coroutines.flow.Flow

@Dao
interface PanenDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(listPanen: List<Panen>)

    @Query("SELECT * FROM panen")
    fun getAll(): Flow<List<Panen>>
}