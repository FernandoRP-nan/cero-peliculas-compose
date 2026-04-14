package com.example.cero.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, CardEntity::class, CardExpenseEntity::class],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun walletDao(): WalletDao
}
