package core

import java.lang.Math.min
import java.util.*


data class Order(val price: Double, val amount: Double, val agent: Agent) : Comparable<Order> {
    override fun compareTo(other: Order): Int {
        return price.compareTo(other.price)
    }
}

class OrderBook(private val p: Product, private val c: Product) {
    /**
     * Buy/sell p for c
     *
     * All prices in c/p
     */

    val bids = PriorityQueue<Order>(reverseOrder())
    val asks = PriorityQueue<Order>()
    private val trades = mutableListOf<Order>()


    fun topBid(): Order? {
        return bids.peek()
    }

    fun topAsk(): Order? {
        return asks.peek()
    }

    fun bids(): List<Order> {
        return bids.sorted()
    }

    fun asks(): List<Order> {
        return asks.sorted()
    }

    override fun toString(): String {
        return "bids: ${bids.peek()}\nasks: ${asks.peek()}"
    }

    fun buy(buyAmount: Double, buyPrice: Double, buyer: Agent) {
        assert(buyAmount > 0)
        assert(buyPrice > 0)
        var buyFor = buyPrice * buyAmount
        //assert(buyer.hasAmount(c, buyFor))

        var buyAmount = buyAmount
        while (buyFor > 1e-3 && !asks.isEmpty()) {
            var (sellPrice, sellAmount, seller) = asks.poll()

            if (sellPrice > buyPrice) { // best offer is not good enough
                asks.offer(Order(sellPrice, sellAmount, seller))
                break
            }

            val willingToBuy = buyFor / sellPrice

            val bought = exchange(buyer, seller, sellPrice, willingToBuy, sellAmount)
            buyFor -= bought * sellPrice
            buyAmount -= bought
            sellAmount -= bought
            if (sellAmount > 0) {
                asks.offer(Order(sellPrice, sellAmount, seller))
            }
        }
        if (buyAmount > 0) {
            bids.offer(Order(buyPrice, buyAmount, buyer))
        }
    }

    fun sell(sellAmount: Double, sellPrice: Double, seller: Agent) {
        assert(seller.hasAmount(p, sellAmount))

        var sellAmount = sellAmount
        while (sellAmount > 0 && !bids.isEmpty()) {
            var (buyPrice, buyAmount, buyer) = bids.poll()

            if (sellPrice > buyPrice) {
                bids.add(Order(buyPrice, buyAmount, buyer))
                break
            }

            val bought = exchange(buyer, seller, buyPrice, buyAmount, sellAmount)
            buyAmount -= bought
            sellAmount -= bought
            if (buyAmount > 0) {
                bids.offer(Order(buyPrice, buyAmount, buyer))
            }
        }

        if (sellAmount > 0) {
            asks.offer(Order(sellPrice, sellAmount, seller))
        }
    }

    private fun exchange(buyer: Agent, seller: Agent, price: Double, buyAmount: Double, sellAmount: Double): Double {
        assert(seller != buyer)
        assert(price > 0)
        val amount = min(buyAmount, sellAmount)
        assert(amount > 0)

        trades.add(Order(price, amount, buyer))

        val total = amount * price

        seller.remove(p, amount)
        buyer.add(p, amount)

        buyer.remove(c, total)
        seller.add(c, total)

        return amount
    }


}