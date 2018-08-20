package main

import kotlin.math.pow

/**
 * TODO
 */
fun main(args: Array<String>) {
    val sellers = (0..999).map { Agent(mutableMapOf(Product.PIZZA to 12.0)) }
    val buyers = (0..999).map { Agent(mutableMapOf(Product.COLA to 12.0)) }
    val agents = buyers.plus(sellers).shuffled()

    auction(agents)
}

fun guess(agents: List<Agent>) {
    for (i in 0..99) {
        val book = OrderBook(Product.PIZZA, Product.COLA)
        agents.shuffled().forEach {
            it.trade(book)
        }
        printState(book, agents)
    }
}

fun auction(agents: List<Agent>) {
    val auctioneer = Auctioneer()
    val guess = 10.0.pow(Math.random() * 2 - 1)

    var last = guess
    for (i in 0..9) {
        val book = OrderBook(Product.PIZZA, Product.COLA)
        val price = auctioneer.discoverPrices(agents, last)
        for (agent in agents) {
            agent.trade(book, price)
        }
        last = price
        printState(book, agents)
    }
}

fun printState(book: OrderBook, agents: List<Agent>) {
    val avgUtility = agents.map { it.utility() }.toDoubleArray().average()
    val variance = agents.map { (it.utility() - avgUtility).pow(2.0) }.toDoubleArray().average()
    println("${book.topBid()?.price}, ${book.topAsk()?.price}, $avgUtility, $variance")
}