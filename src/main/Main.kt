package main

import kotlin.math.pow

/**
 * TODO
 */
fun main(args: Array<String>) {
    val sellers = (0..999).map { Agent(Product.PIZZA, 12.0) }
    sellers.forEach { it.produce() }
    val buyers = (0..999).map { Agent(Product.COLA, 12.0) }
    buyers.forEach { it.produce() }

    val agents = buyers.plus(sellers).shuffled()
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
    }

}