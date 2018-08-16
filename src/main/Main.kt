package main

/**
 * TODO
 */
fun main(args: Array<String>) {
    val sellers = (0..99).map { Agent(Product.PIZZA, 12.0) }
    sellers.forEach { it.produce() }
    val buyers = (0..99).map { Agent(Product.COLA, 12.0) }
    buyers.forEach { it.produce() }

    val book = OrderBook(Product.PIZZA, Product.COLA)

    for ((s, b) in sellers.zip(buyers)) {
        if (Math.random() < 0.5) {
            s.trade(book)
            b.trade(book)
        } else {
            b.trade(book)
            s.trade(book)
        }


    }
    val i = 0

}