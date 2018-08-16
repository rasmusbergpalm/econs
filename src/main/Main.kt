package main

/**
 * TODO
 */
fun main(args: Array<String>) {
    val sellers = (0..999).map { Agent(Product.PIZZA, 12.0) }
    sellers.forEach { it.produce() }
    val buyers = (0..999).map { Agent(Product.COLA, 12.0) }
    buyers.forEach { it.produce() }


    //buyers[0].trade(book)


    for (i in 0..99) {
        val book = OrderBook(Product.PIZZA, Product.COLA)
        sellers.plus(buyers).shuffled().forEach {
            it.trade(book)
        }
        println("head bid: ${book.bids.peek()}")
        println("head ask: ${book.asks.peek()}")
    }

}