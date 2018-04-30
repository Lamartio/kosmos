package io.lamart.kostore

typealias Reducer<T> = (T, Any) -> T

fun <T> reducer(): Reducer<T> = { state, _ -> state }

fun <T> combine(previous: Reducer<T>, next: Reducer<T>): Reducer<T> =
        { state, action -> previous(state, action).let { next(it, action) } }

inline fun <T, reified A> filter(crossinline reducer: (T, A) -> T): Reducer<T> =
        { state, action ->
            if (action is A) reducer(state, action)
            else state
        }

fun <T, R> Reducer<T>.compose(map: (R) -> T, reduce: R.(T) -> R): Reducer<R> = { state: R, action: Any ->
    map(state)
            .let { invoke(it, action) }
            .let { state.reduce(it) }
}