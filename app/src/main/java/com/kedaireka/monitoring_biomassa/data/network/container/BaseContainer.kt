package com.kedaireka.monitoring_biomassa.data.network.container

abstract class BaseContainer<T>{
    abstract val status: Boolean
    abstract val message: String
    abstract val data: List<T>
}
