package io.lamart.kostore.utility

import io.lamart.kostore.Reducer

open class TableReducer<T> : Reducer<T> {

    private var reducer: (T, Any) -> T = { state, _ -> state }

    constructor()

    constructor(init: TableReducer<T>.() -> Unit) : this() {
        init()
    }

    override fun invoke(state: T, action: Any): T = reducer(state, action)

    fun anyState(): Action<T, T> = state<T> { true }

    fun state(statePredicate: T.() -> Boolean): Action<T, T> = state<T>(statePredicate)

    @JvmName("typedState")
    fun <S : T> state(statePredicate: S.() -> Boolean = { true }): Action<T, S> = Action(statePredicate)

    inner class Action<out T, out S : T>(private val statePredicate: S.() -> Boolean) {

        fun withAnyAction(actionPredicate: S.(Any) -> Boolean = { true }): Result<S, Any> =
                withAction(actionPredicate)

        fun withAction(actionPredicate: S.(Any) -> Boolean): Result<S, Any> =
                withAction<Any>(actionPredicate)

        @JvmName("withTypedAction")
        fun <A> withAction(actionPredicate: S.(A) -> Boolean = { true }): Result<S, A> =
                Result(statePredicate, actionPredicate, reducer, { reducer = it })

    }

    inner class Result<in S, in A>(
            val statePredicate: S.() -> Boolean,
            val actionPredicate: S.(A) -> Boolean,
            val reducer: (T, Any) -> T,
            val setReducer: ((T, Any) -> T) -> Unit,
            val result: TableReducer<T> = this@TableReducer
    )
}

@Suppress("NAME_SHADOWING")
inline fun <T, reified S : T, reified A : Any> TableReducer<T>.Result<S, A>.creates(crossinline creator: S.(A) -> T): TableReducer<T> =
        result.apply {
            setReducer { state, action ->
                reducer(state, action).let { state ->
                    if (state is S && action is A && statePredicate(state) && actionPredicate(state, action))
                        creator(state, action)
                    else
                        state
                }
            }
        }

