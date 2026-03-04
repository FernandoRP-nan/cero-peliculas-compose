package com.example.cero.core.network


import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun observe(): Flow<Boolean>

    fun isConnected(): Boolean
}
