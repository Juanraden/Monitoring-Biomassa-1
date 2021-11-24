package com.kedaireka.monitoring_biomassa.data.network

abstract class BaseContainer<T>{
    abstract val status: String
    abstract val message: String
    abstract val data: List<T>
}