package com.kedaireka.monitoring_biomassa.di

import com.kedaireka.monitoring_biomassa.data.domain.*
import com.kedaireka.monitoring_biomassa.database.entity.*
import com.kedaireka.monitoring_biomassa.data.domain.BiotaDomain
import com.kedaireka.monitoring_biomassa.data.domain.KerambaDomain
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.data.domain.PengukuranDomain
import com.kedaireka.monitoring_biomassa.data.network.*
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
    abstract fun bindBiotaNetworkMapper(
        biotaNetworkMapper: BiotaNetworkMapper
    ): EntityMapper<BiotaNetwork, BiotaDomain>

    @Binds
    abstract fun bindKerambaMapper(
        kerambaMapper: KerambaMapper
    ): EntityMapper<Keramba, KerambaDomain>

    @Binds
    abstract fun bindPakanMapper(
        pakanMapper: PakanMapper
    ): EntityMapper<Pakan, PakanDomain>

    @Binds
    abstract fun bindPakanNetworkMapper(
        pakanNetworkMapper: PakanNetworkMapper
    ): EntityMapper<PakanNetwork, PakanDomain>

    @Binds
    abstract fun bindPengukuranMapper(
        pengukuranMapper: PengukuranMapper
    ): EntityMapper<Pengukuran, PengukuranDomain>

    @Binds
    abstract fun bindPengukuranNetworkMapper(
        pengukuranNetworkMapper: PengukuranNetworkMapper
    ): EntityMapper<PengukuranNetwork, PengukuranDomain>

    @Binds
    abstract fun bindFeedingMapper(
        feedingMapper: FeedingMapper
    ): EntityMapper<Feeding, FeedingDomain>

    @Binds
    abstract fun bindFeedingDetailMapper(
        feedingDetailMapper: FeedingDetailMapper
    ): EntityMapper<FeedingDetail, FeedingDetailDomain>
  
    @Binds
    abstract fun bindKerambaNetworkMapper(
        kerambaNetworkMapper: KerambaNetworkMapper
    ): EntityMapper<KerambaNetwork, KerambaDomain>

    @Binds
    abstract fun bindPanenMapper(
        panenMapper: PanenMapper
    ): EntityMapper<Panen, PanenDomain>

    @Binds
    abstract fun bindPanenNetworkMapper(
        panenNetworkMapper: PanenNetworkMapper
    ): EntityMapper<PanenNetwork, PanenDomain>

    @Binds
    abstract fun bindFeedingNetworkMapper(
        feedingNetworkMapper: FeedingNetworkMapper
    ): EntityMapper<FeedingNetwork, FeedingDomain>

    @Binds
    abstract fun bindFeedingDetailNetworkMapper(
        feedingDetailNetworkMapper: FeedingDetailNetworkMapper
    ): EntityMapper<FeedingDetailNetwork, FeedingDetailDomain>
}
