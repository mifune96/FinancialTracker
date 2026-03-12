package com.aliimran.financialtracker.di

import com.aliimran.financialtracker.data.repository.CategoryRepositoryImpl
import com.aliimran.financialtracker.data.repository.TransactionRepositoryImpl
import com.aliimran.financialtracker.domain.repository.CategoryRepository
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their concrete implementations.
 *
 * Using @Binds (rather than @Provides) is preferred here because it generates
 * less bytecode — Hilt only needs to know the implementation class, not build it.
 *
 * Both bindings are @Singleton to match the SingletonComponent lifetime and
 * ensure the same [TransactionRepositoryImpl] / [CategoryRepositoryImpl]
 * instance is reused across all injection sites.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl,
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl,
    ): CategoryRepository
}
