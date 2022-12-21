package com.albertomier.githubapp.repository

import androidx.lifecycle.LiveData
import com.albertomier.githubapp.AppExecutors
import com.albertomier.githubapp.api.ApiResponse
import com.albertomier.githubapp.api.GithubApi
import com.albertomier.githubapp.database.UserDao
import com.albertomier.githubapp.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubApi: GithubApi
) {
    fun loadUser(login: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                userDao.insert(item)
            }

            override fun shouldFetch(data: User): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<User> {
                return userDao.getByLogin(login)
            }

            override fun createCall(): LiveData<ApiResponse<User>> {
                return githubApi.getUser(login)
            }
        }.asLiveData()
    }
}