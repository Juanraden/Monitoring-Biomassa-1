package com.kedaireka.monitoring_biomassa.database.dao

import androidx.room.*
import com.kedaireka.monitoring_biomassa.database.entity.Panen
import com.kedaireka.monitoring_biomassa.database.relation.PanenAndBiota
import kotlinx.coroutines.flow.Flow

@Dao
interface PanenDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listPanen: List<Panen>)

    @Transaction
    @Query("SELECT * FROM panen WHERE keramba_id =:kerambaId")
    fun getAllPanenAndBiota(kerambaId: Int): Flow<List<PanenAndBiota>>

    @Query("SELECT COUNT(*) FROM panen WHERE keramba_id =:kerambaId")
    fun getPanenCountFromKeramba(kerambaId: Int): Int

    @Query("DELETE FROM panen WHERE keramba_id =:kerambaId")
    suspend fun deletePanenFromKeramba(kerambaId: Int)
}