package com.albertomier.githubapp.di

import com.albertomier.githubapp.ui.repo.RepoFragment
import com.albertomier.githubapp.ui.search.SearchFragment
import com.albertomier.githubapp.ui.user.UserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): RepoFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}