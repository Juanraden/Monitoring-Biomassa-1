package com.kedaireka.monitoring_biomassa.repository

import android.content.SharedPreferences
import com.kedaireka.monitoring_biomassa.data.network.LoggedInUser
import com.kedaireka.monitoring_biomassa.data.network.LoginContainer
import com.kedaireka.monitoring_biomassa.service.MonitoringService
import com.kedaireka.monitoring_biomassa.data.network.Result
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository @Inject constructor(
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences
) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
//        dataSource.logout()
    }

    suspend fun loginUser(username: String, password: String): Result<LoggedInUser> {
        val response: Response<LoginContainer> = monitoringService.loginUser(username, password).await()

        return if (response.code() == 200){
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

        return if (!(token.isNullOrBlank() || userId.isNullOrBlank() || username.isNullOrBlank())){
            Result.Success(
                LoggedInUser(
                    token, username, userId
                ))
        } else {
            Result.Error(Exception(""))
        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser

        with(sharedPreferences.edit()){
            putString("token", loggedInUser.token)

            putString("user_id", loggedInUser.user_id)

            putString("username", loggedInUser.username)

            apply()
        }
        // @see https://developer.android.com/training/articles/keystore
    }
}