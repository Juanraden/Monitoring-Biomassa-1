package com.kedaireka.monitoring_biomassa.di

import android.content.Context
import androidx.room.Room
import com.kedaireka.monitoring_biomassa.database.DatabaseBiomassa
import com.kedaireka.monitoring_biomassa.database.dao.BiotaDAO
import com.kedaireka.monitoring_biomassa.database.dao.KerambaDAO
import com.kedaireka.monitoring_biomassa.database.dao.PakanDAO
import com.kedaireka.monitoring_biomassa.database.dao.PengukuranDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): DatabaseBiomassa{
        return Room.databaseBuilder(
            context,
            DatabaseBiomassa::class.java,
            DatabaseBiomassa.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideKerambaDAO(databaseBiomassa: DatabaseBiomassa): KerambaDAO{
        return databaseBiomassa.kerambaDAO()
    }

    @Singleton
    @Provides
    fun provideBiotaDAO(databaseBiomassa: DatabaseBiomassa): BiotaDAO {
        return databaseBiomassa.biotaDAO()
    }

    @Singleton
    @Provides
    fun providePakanDao(databaseBiomassa: DatabaseBiomassa): PakanDAO{
        return databaseBiomassa.pakanDAO()
    }

    @Singleton
    @Provides
    fun providePengukuranDao(databaseBiomassa: DatabaseBiomassa): PengukuranDAO{
        return databaseBiomassa.pengukuranDAO()
    }
}