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

    fun tick(book: OrderBook) {
        utility = 0.0
        produce()
        trade(book)
        consume()
    }

    fun trade(book: OrderBook) {
        val pizzaAmount = stored[Product.PIZZA] ?: 0.0
        val colaAmount = stored[Product.COLA] ?: 0.0
        if (pizzaAmount > 0.0) {
            val sellAmount = pizzaAmount / 100.0
            var expectedPizza = pizzaAmount
            var expectedCola = colaAmount
            for (i in (0..99)) {
                val price = 1.0 * computePrice(expectedPizza, expectedCola, sellAmount)

                expectedPizza -= sellAmount
                expectedCola += sellAmount * price
                book.sell(sellAmount, price, this)
            }
        }
        if (colaAmount > 0.0) {
            val sellAmount = -colaAmount / 100.0
            var expectedPizza = pizzaAmount
            var expectedCola = colaAmount
            for (i in (0..99)) {
                val price = 1.0 * computePrice(expectedPizza, expectedCola, sellAmount)

                expectedPizza -= sellAmount
                expectedCola += sellAmount * price
                book.buy(-sellAmount, price, this)
            }
        }
    }

    /**
     * prev utility < new utility
     * sqrt(p) + sqrt(c) < sqrt(p-a) + sqrt(c + x*a)
     * sqrt(p) + sqrt(c) - sqrt(p-a) < sqrt(c + x*a)
     * (sqrt(p) + sqrt(c) - sqrt(p-a))^2 < c + x*a
     * (sqrt(p) + sqrt(c) - sqrt(p-a))^2 - c < x*a
     * ((sqrt(p) + sqrt(c) - sqrt(p-a))^2 - c)/a < x
     */
    private fun computePrice(p: Double, c: Double, a: Double): Double {
        assert(a <= p)
        return (((Math.sqrt(p) + Math.sqrt(c) - Math.sqrt(p - a)).pow(2.0) - c) / a)

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