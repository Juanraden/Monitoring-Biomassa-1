package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Feeding
import com.kedaireka.monitoring_biomassa.database.entity.FeedingDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDetailDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(feedingDetail: FeedingDetail)

    @Update
    suspend fun updateOne(feeding: Feeding)

    @Query("SELECT * FROM feeding_detail WHERE feeding_id =:feeding_id ORDER BY waktu_feeding DESC LIMIT 10")
    fun getAll(feeding_id: Int): Flow<List<FeedingDetail>>
}