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

class Agent constructor(
        private val inventory: MutableMap<Product, Double> = mutableMapOf()
) {

    fun amount(p: Product): Double {
        return inventory[p] ?: 0.0
    }

    fun hasAmount(p: Product, amount: Double): Boolean {
        return (inventory[p] ?: 0.0) > amount
    }

    private fun guessPrice(book: OrderBook): Double {
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
        return price
    }

    fun trade(book: OrderBook) {
        val price = guessPrice(book)
        trade(book, price)
    }

    /*
        Finds optimal amounts of N products, [x_1, ..., x_N],
        given prices [p_1, ..., p_N] and initial endowments [e_1, ..., e_N]

        Current budget (assuming we can sell it all):
            b = sum_i p_i*e_i
        Utility:
            u(x) = sum_i sqrt(x_i)
        Optimization:
            max u(x) s.t. sum_i p_i*x_i = b

        Solve using lagrange multiplier:
            L(x, λ) = sum_i sqrt(x_i) + λ*sum_i p_i*x_i - b
            1. Set all dL/dx_i = 0, solve for x_i => x_i = (1/(-2λp_i)^2)
            2. Set dL/dλ = 0, insert all x_i from 1. Solve for λ.
            3. Insert λ into all x_i from 1. to get amounts.

        Result:
            v = prod_j p_j
            x_i = (b * v)/(p_i^2*(sum_j v/p_j))
     */
    fun computeAmounts(prices: Map<Product, Double>): Map<Product, Double> {
        assert(prices.keys == Product.values().toSet(), { "Prices missing." })
        assert(prices[Product.values().last()] == 1.0, { "Last product must have price 1.0 (it's the unit of value)" })

        val budget = Product.values().map { prices[it]!! * inventory[it]!! }.sum()
        val priceProduct = prices.values.fold(1.0, { a, p -> a * p })
        val denom = (Product.values().map { priceProduct / prices[it]!! }.sum())

        val amounts = mutableMapOf<Product, Double>()
        for (product in Product.values()) {
            val p = prices[product]!!
            val amount = (budget * priceProduct) / (p.pow(2.0) * denom)
            amounts[product] = amount
        }
        return amounts
    }

    // assuming price is x (cola/expectedPizza)
    // max sqrt(p-a) + sqrt(c+x*a)
    // given a=-2
    // sqrt(p+2) + sqrt(c-2*x) trade 2x coke for 2 expectedPizza
    // given a=3
    // sqrt(p-3) + sqrt(c+3*x) trade 3 expectedPizza for 3x cola
    // a = -(c - p x^2)/(x^2 + x)
    fun computeAmount(price: Double): Double {
        val p = inventory[Product.PIZZA] ?: 0.0
        val c = inventory[Product.COLA] ?: 0.0
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
        var utility = 0.0
        for (v in inventory.values) {
            utility += sqrt(v)
        }
        return utility
    }

    fun remove(p: Product, amount: Double) {
        val left = (inventory[p] ?: 0.0) - amount
        assert(left > 0)
        inventory[p] = left
    }

    fun add(p: Product, amount: Double) {
        inventory[p] = (inventory[p] ?: 0.0) + amount
    }
}