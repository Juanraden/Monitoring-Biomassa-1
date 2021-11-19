package com.kedaireka.monitoring_biomassa.di

import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.database.entity.Biota
import com.kedaireka.monitoring_biomassa.database.entity.Keramba
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import com.kedaireka.monitoring_biomassa.database.entity.Pengukuran
import com.kedaireka.monitoring_biomassa.util.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {

    @Binds
    abstract fun bindBiotaMapper(
        biotaMapper: BiotaMapper
    ): EntityMapper<Biota, BiotaDomain>

    @Binds
    abstract fun bindKerambaMapper(
        kerambaMapper: KerambaMapper
    ): EntityMapper<Keramba, KerambaDomain>

    @Binds
    abstract fun bindPakanMapper(
        pakanMapper: PakanMapper
    ): EntityMapper<Pakan, PakanDomain>

    @Binds
    abstract fun bindPengukuranMapper(
        pengukuranMapper: PengukuranMapper
    ): EntityMapper<Pengukuran, PengukuranDomain>
}