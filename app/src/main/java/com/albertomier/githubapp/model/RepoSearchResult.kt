package com.albertomier.githubapp.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.albertomier.githubapp.database.GithubTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(GithubTypeConverters::class)
class RepoSearchResult(
    val query: String,
    val reposIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)