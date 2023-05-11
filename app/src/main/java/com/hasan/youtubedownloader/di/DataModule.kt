package com.hasan.youtubedownloader.di

import android.content.Context
import com.hasan.youtubedownloader.data.YoutubeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSharedDownloadManager(
        @ApplicationContext context: Context
    ): YoutubeRepository =
        YoutubeRepository(context)

}