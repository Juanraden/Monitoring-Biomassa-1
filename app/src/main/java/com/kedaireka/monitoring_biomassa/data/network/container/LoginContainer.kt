package com.kedaireka.monitoring_biomassa.data.network.container

import androidx.annotation.Keep
import com.kedaireka.monitoring_biomassa.data.network.LoggedInUser
import com.squareup.moshi.JsonClass


@Keep
@JsonClass(generateAdapter = true)
data class LoginContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<LoggedInUser>
): BaseContainer<LoggedInUser>()