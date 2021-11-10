package com.kedaireka.monitoring_biomassa.util

interface EntityMapper<Entity, Target> {
    fun mapFromEntity(entity: Entity): Target

    fun mapToEntity(target: Target): Entity
}