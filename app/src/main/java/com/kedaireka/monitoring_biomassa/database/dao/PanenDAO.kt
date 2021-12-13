package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Panen
import com.kedaireka.monitoring_biomassa.database.relation.PanenAndBiota
import kotlinx.coroutines.flow.Flow

@Dao
interface PanenDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(listPanen: List<Panen>)

    @Transaction
    @Query("SELECT * FROM panen")
    fun getAllPanenAndBiota(): Flow<List<PanenAndBiota>>
}