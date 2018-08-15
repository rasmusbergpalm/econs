package main

/**
 * TODO
 */
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow
import kotlin.math.sqrt

enum class Product {
    PIZZA,
    COLA
}

private val r = ThreadLocalRandom.current()!!

class Agent(
        private val produces: Product = Product.values()[r.nextInt(Product.values().size)],
        private val amount: Double = (10.0).pow(r.nextDouble(0.0, 2.0))
) {
    private val stored = hashMapOf<Product, Double>()

    init {
        for (product in Product.values()) {
            stored[product] = 0.0
        }
    }

    private var utility = 0.0

    fun amount(p: Product): Double {
        return stored[p] ?: 0.0
    }

    fun hasAmount(p: Product, amount: Double): Boolean {
        return (stored[p] ?: 0.0) > amount
    }

    fun tick() {
        utility = 0.0
        produce()
        consume()
    }

    fun utility(): Double {
        return utility
    }

    private fun consume() {
        for ((p, v) in stored) {
            stored[p] = 0.0
            utility += sqrt(v)
        }
    }

    fun produce() {
        stored[produces] = (stored[produces] ?: 0.0) + amount
    }

    fun remove(p: Product, amount: Double) {
        val left = (stored[p] ?: 0.0) - amount
        assert(left > 0)
        stored[p] = left
    }

    fun add(p: Product, amount: Double) {
        stored[p] = (stored[p] ?: 0.0) + amount
    }
}