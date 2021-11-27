package com.kedaireka.monitoring_biomassa.data.network

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LoginContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<LoggedInUser>
): BaseContainer<LoggedInUser>()