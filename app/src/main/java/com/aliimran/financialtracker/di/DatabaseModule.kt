package com.aliimran.financialtracker.di

import android.content.Context
import androidx.room.Room
import com.aliimran.financialtracker.data.local.AppDatabase
import com.aliimran.financialtracker.data.local.dao.CategoryDao
import com.aliimran.financialtracker.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Hilt module providing the Room database and its DAOs.
 *
 * Installed in [SingletonComponent] so the DB instance lives for the
 * entire application lifetime — this avoids the overhead of repeatedly
 * opening and closing the SQLite connection.
 *
 * MIGRATIONS: When the schema changes, add a Migration object here and
 * pass it to addMigrations() on the builder to preserve user data.
 *   e.g. private val MIGRATION_1_2 = object : Migration(1, 2) { ... }
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Qualifier for an application-scoped [CoroutineScope] used exclusively
     * for database operations (e.g., seeding default categories on first run).
     * SupervisorJob ensures one failed child doesn't cancel siblings.
     */
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApplicationScope

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .addCallback(AppDatabase.buildCallback(scope))
            // Uncomment below only during development — destructive migration
            // destroys all user data on schema change!
            // .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideTransactionDao(db: AppDatabase): TransactionDao =
        db.transactionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(db: AppDatabase): CategoryDao =
        db.categoryDao()
}
