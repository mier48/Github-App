package com.albertomier.githubapp.di

import android.app.Application
import androidx.room.Room
import com.albertomier.githubapp.api.GithubApi
import com.albertomier.githubapp.core.AppConstants.BASE_URL
import com.albertomier.githubapp.core.AppConstants.DATABASE_NAME
import com.albertomier.githubapp.database.GithubDB
import com.albertomier.githubapp.database.RepoDao
import com.albertomier.githubapp.database.UserDao
import com.albertomier.githubapp.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideGithubApi(): GithubApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(GithubApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(app: Application): GithubDB {
        return Room.databaseBuilder(app, GithubDB::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: GithubDB): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(database: GithubDB): RepoDao {
        return database.repoDao()
    }
}