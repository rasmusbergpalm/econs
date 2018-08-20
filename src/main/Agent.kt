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

    fun amount(p: Product): Double {
        return stored[p] ?: 0.0
    }

    fun hasAmount(p: Product, amount: Double): Boolean {
        return (stored[p] ?: 0.0) > amount
    }

    fun tick(book: OrderBook) {
        produce()
        trade(book)
    }

    // assuming price is x (cola/pizza)
    // max sqrt(p-a) + sqrt(c+x*a)
    // given a=-2
    // sqrt(p+2) + sqrt(c-2*x) trade 2x coke for 2 pizza
    // given a=3
    // sqrt(p-3) + sqrt(c+3*x) trade 3 pizza for 3x cola
    // a = -(c - p x^2)/(x^2 + x)
    fun computeAmount(p: Double, c: Double, price: Double): Double {
        return -(c - p * price.pow(2.0)) / (price.pow(2.0) + price)
    }

    fun trade(book: OrderBook) {
        val pizzaAmount = stored[Product.PIZZA] ?: 0.0
        val colaAmount = stored[Product.COLA] ?: 0.0


        val ask = book.topAsk()
        val bid = book.topBid()
        val price = if (ask != null && bid != null) {
            val mean = (bid.price + ask.price) / 2
            val logMean = Math.log10(mean)
            10.0.pow(r.nextGaussian() / 10.0 + logMean)
        } else {
            val logPrice = (Math.random() * 3.0) - 2.0
            10.0.pow(logPrice)
        }

        val amount = computeAmount(pizzaAmount, colaAmount, price)
        if (amount > 0) {
            book.sell(amount, price, this)
        }
        if (amount < 0) {
            book.buy(-amount, price, this)
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
        var utility = 0.0
        for ((p, v) in stored) {
            utility += sqrt(v)
        }
        return utility
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