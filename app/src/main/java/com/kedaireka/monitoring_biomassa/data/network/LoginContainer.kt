package com.kedaireka.monitoring_biomassa.data.network


data class LoginContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<LoggedInUser>
): BaseContainer<LoggedInUser>()