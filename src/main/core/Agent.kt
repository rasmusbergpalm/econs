package core

/**
 * TODO
 */
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow
import kotlin.math.sqrt

private val r = ThreadLocalRandom.current()!!

class Agent constructor(
        val inventory: MutableMap<Product, Double> = mutableMapOf()
) {

    fun amount(p: Product): Double {
        return inventory[p] ?: 0.0
    }

    fun hasAmount(p: Product, amount: Double): Boolean {
        return (inventory[p] ?: 0.0) >= amount
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

    private fun guessPrices(market: Market): Map<Product, Double> {
        return market.books.map { it.key to guessPrice(it.value) }
                .plus(market.unitOfValue to 1.0)
                .toMap()
    }

    fun trade(market: Market) {
        val prices = guessPrices(market)
        trade(market, prices)
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
    fun desiredAmounts(prices: Map<Product, Double>, unitOfValue: Product): Map<Product, Double> {
        val prices = prices.plus(unitOfValue to 1.0)

        val budget = prices.map { it.value * (inventory[it.key] ?: 0.0) }.sum()
        val priceProduct = prices.values.fold(1.0, { a, p -> a * p })
        val denom = prices.values.map { priceProduct / it }.sum()

        val amounts = mutableMapOf<Product, Double>()
        for ((product, price) in prices) {
            val amount = (budget * priceProduct) / (price.pow(2.0) * denom)
            amounts[product] = amount
        }
        return amounts
    }

    fun excessDemand(prices: Map<Product, Double>, unitOfValue: Product): Map<Product, Double> {
        return desiredAmounts(prices, unitOfValue).mapValues { it.value - (inventory[it.key] ?: 0.0) }
    }

    fun trade(market: Market, prices: Map<Product, Double>) {
        val unitOfValue = market.unitOfValue
        val demands = excessDemand(prices, unitOfValue).minus(unitOfValue)

        //var leftToBuyFor = (inventory[unitOfValue] ?: 0.0) - (demands[unitOfValue] ?: 0.0)
        for ((product, demand) in demands.toList().sortedBy { it.second }) {
            val p = prices[product]!!

            if (demand < 0) {
                market.sell(product, -demand, p, this)
            }
            if (demand > 0) {
                //val canAfford = leftToBuyFor / p //gold/(gold/pizza) = pizza
                //val buyAmount = Math.min(canAfford, demand)
                //leftToBuyFor -= buyAmount * p
                market.buy(product, demand, p, this)
            }

        }
    }

    fun utility(): Double {
        return inventory.values.map { sqrt(it) }.sum()
    }

    fun remove(p: Product, amount: Double) {
        val left = (inventory[p] ?: 0.0) - amount
        //assert(left >= 0)
        inventory[p] = left
    }

    fun add(p: Product, amount: Double) {
        inventory[p] = (inventory[p] ?: 0.0) + amount
    }
}