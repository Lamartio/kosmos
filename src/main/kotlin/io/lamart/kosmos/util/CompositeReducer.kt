package io.lamart.kosmos.util

import io.lamart.kosmos.Reducer

open class CompositeReducer<T> : Reducer<T> {

    private var reducer: (T, Any) -> T = { state, action -> state }

    constructor(vararg reducers: (T, Any) -> T) {
        reducers.forEach { add(it) }
    }

    constructor(init: CompositeReducer<T>.() -> Unit) {
        init()
    }

    override fun invoke(state: T, action: Any): T = reducer(state, action)

    fun add(reducer: (T, Any) -> T): CompositeReducer<T> = apply {
        this.reducer = Reducer.combine(this.reducer, reducer)
    }

}