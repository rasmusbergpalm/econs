/**
 * TODO
 */
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow
import kotlin.math.sqrt

enum class Product {
    GRAIN,
    RICE,
    MILK,
    CLOTHING,
    COFFEE,
    GOLD,
    FURNITURE,
    CHOCOLATE
}


class Agent {
    private val r = ThreadLocalRandom.current()!!

    private val produces: Product = Product.values()[r.nextInt(Product.values().size)]
    private val amount = (10.0).pow(r.nextDouble(0.0, 2.0))

    private val stored = hashMapOf<Product, Double>()

    init {
        for (product in Product.values()) {
            stored[product] = 0.0
        }
    }

    private var utility = 0.0

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

    private fun produce() {
        stored[produces] = (stored[produces] ?: 0.0) + amount
    }
}