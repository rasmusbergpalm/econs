package main

import kotlin.math.pow

class Auctioneer {

    fun discoverPrices(agents: List<Agent>, initial: Double): Double {
        var logPrice = Math.log10(initial)
        val p = 0.00001
        for (i in 0..99) {
            var excessSupply = 0.0
            for (agent in agents) {
                excessSupply += agent.computeAmount(10.0.pow(logPrice))
            }
            val d = p * excessSupply
            if (Math.abs(d) < 1e-8) {
                break
            }
            logPrice -= p * excessSupply
        }
        return 10.0.pow(logPrice)
    }
}