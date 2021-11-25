package com.kedaireka.monitoring_biomassa.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kedaireka.monitoring_biomassa.database.dao.*
import com.kedaireka.monitoring_biomassa.database.entity.*

@Database(entities = [Keramba::class, Biota::class, Pakan::class, Pengukuran::class, Feeding::class, FeedingDetail::class], version = 1, exportSchema = false)
abstract class DatabaseBiomassa: RoomDatabase() {
    abstract fun kerambaDAO(): KerambaDAO

    abstract fun biotaDAO(): BiotaDAO

    abstract fun pakanDAO(): PakanDAO

    abstract fun pengukuranDAO(): PengukuranDAO

    abstract fun feedingDAO(): FeedingDAO

    abstract fun feedingDetailDAO(): FeedingDetailDAO

    companion object{
        const val DATABASE_NAME = "biomassa_db"
    }
}