package io.lamart.kosmos

class CompositeInterceptor<T>(private val root: StoreSource<T>) : StoreSource<T> {

    var router: (StoreSource<T>) -> StoreSource<T> = { it }

    override val state: T = router(root).state

    override fun invoke(): T = router(root).invoke()

    override fun invoke(action: Any) = router(root).invoke(action)

    fun add(router: (StoreSource<T>) -> StoreSource<T>): CompositeInterceptor<T> =
            apply { this.router = combineInterceptors(this.router, router) }

    companion object {

        fun <T> combineInterceptors(
                previous: (StoreSource<T>) -> StoreSource<T>,
                next: (StoreSource<T>) -> StoreSource<T>
        ): (StoreSource<T>) -> StoreSource<T> = { it.let(previous).let(next) }

    }

}