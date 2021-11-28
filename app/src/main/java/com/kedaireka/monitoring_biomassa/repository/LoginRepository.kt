package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import com.kedaireka.monitoring_biomassa.data.network.LoggedInUser
import com.kedaireka.monitoring_biomassa.data.network.LoginContainer
import com.kedaireka.monitoring_biomassa.data.network.Result
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

@Singleton
class LoginRepository @Inject constructor(
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences
) {
    suspend fun loginUser(username: String, password: String): Result<LoggedInUser> {
        val response: Response<LoginContainer> =
            monitoringService.loginUserAsync(username, password).await()

        return if (response.code() == 200) {
            val loggedInUser: LoggedInUser = response.body()!!.data[0]

            setLoggedInUser(loggedInUser)

            Result.Success(loggedInUser)
        } else {
            Result.Error(Exception(response.body()!!.message))
        }
    }

    fun isUserLoggedIn(): Result<LoggedInUser> {
        val token = sharedPreferences.getString("token", "")
        val userId = sharedPreferences.getString("user_id", "")
        val username = sharedPreferences.getString("username", "")

        return if (!(token.isNullOrBlank() || userId.isNullOrBlank() || username.isNullOrBlank())) {
            Result.Success(
                LoggedInUser(
                    token, username, userId
                )
            )
        } else {
            Result.Error(Exception(""))
        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        sharedPreferences.edit()

            .putString("token", loggedInUser.token)

            .putString("user_id", loggedInUser.user_id)

            .putString("username", loggedInUser.username)

            .apply()
        // @see https://developer.android.com/training/articles/keystore
    }

    fun logOutUser(){
        sharedPreferences.edit().clear().apply()
    }
}