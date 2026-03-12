package com.aliimran.financialtracker.domain.usecase.base

import com.aliimran.financialtracker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Base class for use cases that expose a reactive [Flow] stream.
 *
 * Every upstream emission is wrapped in [Resource.Success]; any exception
 * is caught and re-emitted as [Resource.Error], so ViewModels never crash
 * on unhandled repository exceptions.
 *
 * Usage:
 *   class MyUseCase @Inject constructor(...) :
 *       FlowUseCase<MyUseCase.Params, List<Foo>>() {
 *
 *       override fun execute(params: Params): Flow<List<Foo>> = ...
 *       data class Params(val id: Long)
 *   }
 */
abstract class FlowUseCase<in P, R> {

    operator fun invoke(params: P): Flow<Resource<R>> =
        execute(params)
            .map<R, Resource<R>> { Resource.Success(it) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }

    protected abstract fun execute(params: P): Flow<R>
}

/** Variant with no input parameters. */
abstract class NoParamFlowUseCase<R> {

    operator fun invoke(): Flow<Resource<R>> =
        execute()
            .map<R, Resource<R>> { Resource.Success(it) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }

    protected abstract fun execute(): Flow<R>
}
