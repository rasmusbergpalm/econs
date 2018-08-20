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

    for (i in 0..99) {
        val book = OrderBook(Product.PIZZA, Product.COLA)
        val agents = sellers.plus(buyers).shuffled()
        agents.forEach {
            it.trade(book)
        }

        val avgUtility = agents.map { it.utility() }.toDoubleArray().average()
        val variance = agents.map { (it.utility() - avgUtility).pow(2.0) }.toDoubleArray().average()
        println("${book.topBid()?.price}, ${book.topAsk()?.price}, $avgUtility, $variance")
    }
}