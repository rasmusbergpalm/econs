package core

import kotlin.math.pow

/**
 * TODO
 */
fun main(args: Array<String>) {
    val agents = mutableListOf<Agent>()
    for (product in Product.values()) {
        agents.addAll((0..999).map { Agent(mutableMapOf(product to 9.0)) })
    }

    auction(agents)
}

fun guess(agents: List<Agent>) {
    for (i in 0..99) {
        val prevUtilities = agents.map { it to it.utility() }.toMap()
        val market = Market(Product.values().asList().minus(Product.GOLD).toSet(), Product.GOLD)
        agents.shuffled().forEach {
            it.trade(market)
        }
        for (agent in agents) {
            assert(agent.utility() >= prevUtilities[agent]!!)
        }
        printState(market, agents)
    }
}

fun auction(agents: List<Agent>) {
    val auctioneer = Auctioneer()
    val unitOfValue = Product.GOLD
    val products = Product.values().asList().minus(unitOfValue).toSet()

    val guess = products.map { it to 10.0.pow(Math.random() * 2 - 1) }.toMap()

    printState(Market(products, unitOfValue), agents)

    val market = Market(products, unitOfValue)
    val prices = auctioneer.discoverPrices(agents, guess, unitOfValue)
    for (agent in agents.shuffled()) {
        agent.trade(market, prices)
    }
    printState(market, agents)
}

fun printState(market: Market, agents: List<Agent>) {
    val avgUtility = agents.map { it.utility() }.toDoubleArray().average()
    val variance = agents.map { (it.utility() - avgUtility).pow(2.0) }.toDoubleArray().average()
    println("UTILITY: $avgUtility +- $variance")
    for ((product, book) in market.books) {
        println("$product ${book.topBid()?.price}, ${book.topAsk()?.price}. Supply: ${book.asks().sumByDouble { it.amount }}. Demand: ${book.bids().sumByDouble { it.amount }}")
    }

}