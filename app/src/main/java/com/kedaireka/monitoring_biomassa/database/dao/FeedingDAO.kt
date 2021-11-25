package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Feeding
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(feeding: Feeding)

    @Update
    suspend fun updateOne(feeding: Feeding)

    @Query("SELECT * FROM feeding WHERE feeding_id =:feeding_id ORDER BY tanggal_feeding DESC LIMIT 10")
    fun getAll(feeding_id: Int): Flow<List<Feeding>>

    @Query("SELECT * FROM feeding WHERE feeding_id =:feeding_id")
    fun getById(feeding_id: Int): Flow<Feeding>
}