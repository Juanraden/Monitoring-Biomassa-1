package com.kedaireka.monitoring_biomassa.data.network.enums

sealed class NetworkResult(val message: String = ""){
    class Loaded(): NetworkResult()
    class Loading(): NetworkResult()
    class Error(message: String): NetworkResult(message)
}
