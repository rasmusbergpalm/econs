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
        consume()
    }

    // assuming price is x (cola/expectedPizza)
    // max sqrt(p-a) + sqrt(c+x*a)
    // given a=-2
    // sqrt(p+2) + sqrt(c-2*x) trade 2x coke for 2 expectedPizza
    // given a=3
    // sqrt(p-3) + sqrt(c+3*x) trade 3 expectedPizza for 3x cola
    // a = -(c - p x^2)/(x^2 + x)
    fun computeAmount(price: Double): Double {
        val p = stored[Product.PIZZA] ?: 0.0
        val c = stored[Product.COLA] ?: 0.0
        return -(c - p * price.pow(2.0)) / (price.pow(2.0) + price)
    }

    fun trade(book: OrderBook, price: Double) {
        val amount = computeAmount(price)
        if (amount > 0) {
            book.sell(amount, price, this)
        }
        if (amount < 0) {
            book.buy(-amount, price, this)
        }
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