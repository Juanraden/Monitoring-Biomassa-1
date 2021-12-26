package com.kedaireka.monitoring_biomassa.data.network.enums

sealed class NetworkResult(val message: String = ""){
    class Initial : NetworkResult()
    class Loaded(message: String = ""): NetworkResult(message)
    class Loading : NetworkResult()
    class Error(message: String): NetworkResult(message)
}
