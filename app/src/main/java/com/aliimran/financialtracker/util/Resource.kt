package com.aliimran.financialtracker.util

/**
 * Generic wrapper for UI state that can be Loading, Success, or Error.
 *
 * Used as the emission type of [FlowUseCase] so ViewModels have a single
 * type to switch on for driving UI state.
 */
sealed class Resource<out T> {

    /** Represents an in-flight asynchronous operation. */
    data object Loading : Resource<Nothing>()

    /**
     * Represents a successfully completed operation.
     * @param data The result payload.
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Represents a failed operation.
     * @param message Human-readable error description.
     * @param cause   Optional originating throwable for logging.
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null,
    ) : Resource<Nothing>()
}

/** Returns true only when this is a [Resource.Loading]. */
val Resource<*>.isLoading get() = this is Resource.Loading

/** Returns the data if [Resource.Success], otherwise null. */
fun <T> Resource<T>.dataOrNull(): T? = (this as? Resource.Success)?.data
