package com.kedaireka.monitoring_biomassa.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kedaireka.monitoring_biomassa.database.dao.BiotaDAO
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.database.entity.Keramba

@Database(entities = [Keramba::class, Biota::class], version = 1, exportSchema = false)
abstract class DatabaseBiomassa: RoomDatabase() {
    abstract fun KerambaDAO(): KerambaDAO

    abstract fun BiotaDAO(): BiotaDAO

    companion object{
        val DATABASE_NAME = "biomassa_db"
    }
}