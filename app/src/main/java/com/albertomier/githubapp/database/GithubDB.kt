package com.albertomier.githubapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.albertomier.githubapp.model.Contributor
import com.albertomier.githubapp.model.Repo
import com.albertomier.githubapp.model.RepoSearchResult
import com.albertomier.githubapp.model.User

@Database(
    entities = [User::class, Repo::class, Contributor::class, RepoSearchResult::class],
    version = 1
)
abstract class GithubDB : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao
}