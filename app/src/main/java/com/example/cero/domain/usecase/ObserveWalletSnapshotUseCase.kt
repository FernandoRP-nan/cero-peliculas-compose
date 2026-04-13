package com.example.cero.domain.usecase

import com.example.cero.domain.repository.WalletRepository
import javax.inject.Inject

class ObserveWalletSnapshotUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    operator fun invoke() = repository.observeWalletSnapshot()
}
