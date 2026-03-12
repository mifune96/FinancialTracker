package com.aliimran.financialtracker.domain.usecase.base

import com.aliimran.financialtracker.util.Resource

/**
 * Base class for one-shot suspend use cases (insert / update / delete).
 * Returns [Resource.Success] on completion or [Resource.Error] on exception.
 */
abstract class SuspendUseCase<in P, R> {

    suspend operator fun invoke(params: P): Resource<R> =
        try {
            Resource.Success(execute(params))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }

    @Throws(Exception::class)
    protected abstract suspend fun execute(params: P): R
}
