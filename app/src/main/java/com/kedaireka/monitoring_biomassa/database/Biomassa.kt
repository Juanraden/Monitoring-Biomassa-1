package com.kedaireka.monitoring_biomassa.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kedaireka.monitoring_biomassa.database.dao.BiotaDAO
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.dao.PakanDAO
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import com.kedaireka.monitoring_biomassa.database.entity.Pengukuran

@Database(entities = [Keramba::class, Biota::class, Pakan::class, Pengukuran::class], version = 1, exportSchema = false)
abstract class DatabaseBiomassa: RoomDatabase() {
    abstract fun kerambaDAO(): KerambaDAO

    abstract fun biotaDAO(): BiotaDAO

    abstract fun pakanDAO(): PakanDAO



    companion object{
        const val DATABASE_NAME = "biomassa_db"
    }
}