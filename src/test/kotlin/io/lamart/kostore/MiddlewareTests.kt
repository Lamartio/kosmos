package io.lamart.kostore

import io.lamart.kostore.input.IntWrapper
import io.lamart.kostore.utils.test
import org.junit.Assert.assertEquals
import org.junit.Test

class MiddlewareTests {

    private val state: Int get() = throw NotImplementedError("You should not use this")
    private fun dispatch(action: Any): Unit = throw NotImplementedError("You should not use this")

    private val flipMathMiddleware: Middleware<Int> = { _, _, action, next ->
        val newAction = when (action) {
            "increment" -> "decrement"
            "decrement" -> "increment"
            else -> action
        }

        next(newAction)
    }

    @Test
    fun middlewareTester() {
        flipMathMiddleware
                .test()
                .dispatch("increment")
                .invoke()
                .run { results }
                .also { assertEquals("decrement", it.first()) }
    }

    @Test
    fun combineMiddleware() {
        combine(flipMathMiddleware, flipMathMiddleware)
                .test()
                .dispatch("increment")
                .invoke()
                .run { results }
                .also { assertEquals("increment", it.first()) }
    }

    @Test
    fun beforeMiddleware() {
        val list = mutableListOf<String>()

        beforeNext<Int>({ _, _, _, _ -> list.add("beforeNext") })
                .invoke(::state, ::dispatch, "increment", { list.add("next") })

        assertEquals("beforeNext", list[0])
        assertEquals("next", list[1])
    }

    @Test
    fun afterMiddleware() {
        val list = mutableListOf<String>()

        afterNext<Int>({ _, _, _, _ -> list.add("afterNext") })
                .invoke(::state, ::dispatch, "increment", { list.add("next") })

        assertEquals("next", list[0])
        assertEquals("afterNext", list[1])
    }

    @Test
    fun beforeAndAfterMiddleware() {
        val list = mutableListOf<String>()

        beforeNext<Int>({ _, _, _, _ -> list.add("beforeNext") })
                .after({ _, _, _, _ -> list.add("afterNext") })
                .invoke(::state, ::dispatch, "increment", { list.add("next") })

        assertEquals("beforeNext", list[0])
        assertEquals("next", list[1])
        assertEquals("afterNext", list[2])
    }

    @Test
    fun compose() {
        val wrapperMiddleware: Middleware<IntWrapper> = flipMathMiddleware.compose { it.number }

        wrapperMiddleware
                .test()
                .dispatch("increment")
                .invoke()
                .run { results }
                .also { assertEquals("decrement", it.first()) }
    }

}