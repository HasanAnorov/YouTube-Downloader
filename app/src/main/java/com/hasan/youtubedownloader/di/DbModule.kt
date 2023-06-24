package com.hasan.youtubedownloader.di

import android.content.Context
import androidx.room.Room
import com.hasan.youtubedownloader.db.AppDatabase
import com.hasan.youtubedownloader.db.Video
import com.hasan.youtubedownloader.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        Constants.VIDEO_DATABASE
    ).allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideDao(db: AppDatabase) = db.userDao()

    @Provides
    fun provideEntity() = Video()

}