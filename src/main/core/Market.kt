package core

class Market private constructor(
        val books: Map<Product, OrderBook>,
        val unitOfValue: Product

) {
    constructor(products: Set<Product>, unitOfValue: Product) : this(
            products.map { it to OrderBook(it, unitOfValue) }.toMap(),
            unitOfValue
    )

    fun buy(p: Product, amount: Double, price: Double, buyer: Agent) {
        books[p]!!.buy(amount, price, buyer)
    }

    fun sell(p: Product, amount: Double, price: Double, seller: Agent) {
        books[p]!!.sell(amount, price, seller)
    }

}