package core

import junit.framework.TestCase
import kotlin.math.pow

/**
 * TODO
 */
class AuctioneerTest : TestCase() {

    fun testDiscoverPrices() {
        val agents = mutableListOf<Agent>()
        for (product in Product.values()) {
            agents.addAll((0..100).map { Agent(mutableMapOf(product to 10.0.pow(30 * Math.random()))) })
        }
        val auctioneer = Auctioneer()

        val unitOfValue = Product.GOLD
        val products = Product.values().asList().minus(unitOfValue).toSet()

        val guess = products.map { it to 10.0.pow(Math.random() * 2 - 1) }.toMap()

        val prices = auctioneer.discoverPrices(agents, guess, unitOfValue)
    }
}