package core

import kotlin.math.pow

class Auctioneer {

    fun sum(m1: Map<Product, Double>, m2: Map<Product, Double>): Map<Product, Double> {
        assert(m1.keys == m2.keys)
        return m1.mapValues { it.value + m2[it.key]!! }
    }

    fun discoverPrices(agents: List<Agent>, initial: Map<Product, Double>, unitOfValue: Product): Map<Product, Double> {
        var logPrices = initial.mapValues { Math.log10(it.value) }
        val p = 0.5
        for (i in 0..999) {
            var excessDemand = logPrices.mapValues { 0.0 }.toMap()
            var demandPlusSupply = logPrices.mapValues { 0.0 }.toMap()
            val prices = logPrices.mapValues { 10.0.pow(it.value) }

            for (agent in agents) {
                val agentExcessDemand = agent.excessDemand(prices, unitOfValue).minus(unitOfValue)
                demandPlusSupply = sum(demandPlusSupply, agentExcessDemand.mapValues { Math.abs(it.value) })
                excessDemand = sum(excessDemand, agentExcessDemand)
            }
            excessDemand = excessDemand.minus(unitOfValue)

            val d = excessDemand.mapValues { p * it.value / demandPlusSupply[it.key]!! }
            if (d.all { Math.abs(it.value) < 1e-8 }) {
                println("Used $i iterations")
                return prices
            }

            logPrices = sum(logPrices, d)
            logPrices.forEach({
                assert(it.value.isFinite())
            })
        }
        throw PricesDidNotConverge()
    }
}

class PricesDidNotConverge : RuntimeException("Prices did not converge!")