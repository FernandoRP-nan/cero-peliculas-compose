package com.example.cero.data.di


import android.content.Context
import androidx.room.Room
import com.example.cero.data.local.AppDatabase
import com.example.cero.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cero_db"
        ).build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao =
        database.userDao()
}