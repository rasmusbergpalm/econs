package test

import junit.framework.TestCase
import main.Agent
import main.Order
import main.OrderBook
import main.Product.COLA
import main.Product.PIZZA

class OrderBookTest : TestCase() {

    fun test_trade() {
        val book = OrderBook(PIZZA, COLA)
        val seller = Agent(PIZZA, 8.0)
        val buyer = Agent(COLA, 12.0)
        buyer.produce()
        seller.produce()

        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3 cola
        book.sell(1.0, 3.0, seller) // sell 1 pizza for 3 cola

        assertEquals(7.0, seller.amount(PIZZA))
        assertEquals(3.0, seller.amount(COLA))

        assertEquals(1.0, buyer.amount(PIZZA))
        assertEquals(9.0, buyer.amount(COLA))

        assertEquals(emptyList<Order>(), book.bids())
        assertEquals(emptyList<Order>(), book.asks())
    }

    fun test_no_trade() {
        val book = OrderBook(PIZZA, COLA)
        val seller = Agent(PIZZA, 8.0)
        val buyer = Agent(COLA, 12.0)
        buyer.produce()
        seller.produce()

        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3 cola
        book.sell(1.0, 4.0, seller) // sell 1 pizza for 4 cola

        assertEquals(8.0, seller.amount(PIZZA))
        assertEquals(0.0, seller.amount(COLA))

        assertEquals(0.0, buyer.amount(PIZZA))
        assertEquals(12.0, buyer.amount(COLA))

        assertEquals(listOf(Order(3.0, 1.0, buyer)), book.bids())
        assertEquals(listOf(Order(4.0, 1.0, seller)), book.asks())
    }

    fun test_partial_trade() {
        val book = OrderBook(PIZZA, COLA)
        val seller = Agent(PIZZA, 8.0)
        val buyer = Agent(COLA, 12.0)
        buyer.produce()
        seller.produce()

        book.buy(2.5, 3.0, buyer) // buy 2.5 pizza for 3.0 cola/pizza
        book.sell(1.0, 3.0, seller) // sell 1 pizza for 3 cola

        assertEquals(7.0, seller.amount(PIZZA))
        assertEquals(3.0, seller.amount(COLA))

        assertEquals(1.0, buyer.amount(PIZZA))
        assertEquals(9.0, buyer.amount(COLA))

        assertEquals(listOf(Order(3.0, 1.5, buyer)), book.bids())
        assertEquals(emptyList<Order>(), book.asks())
    }

    fun test_buyer_buys_at_lowest_prices_available() {
        val book = OrderBook(PIZZA, COLA)
        val seller = Agent(PIZZA, 8.0)
        val buyer = Agent(COLA, 12.0)
        buyer.produce()
        seller.produce()

        book.sell(1.0, 3.0, seller) // sell 1 pizza for 3 cola
        book.sell(1.0, 4.0, seller) // sell 1 pizza for 4 cola
        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3.0 cola/pizza

        assertEquals(7.0, seller.amount(PIZZA))
        assertEquals(3.0, seller.amount(COLA))

        assertEquals(1.0, buyer.amount(PIZZA))
        assertEquals(9.0, buyer.amount(COLA))

        assertEquals(emptyList<Order>(), book.bids())
        assertEquals(listOf(Order(4.0, 1.0, seller)), book.asks())
    }

    fun test_seller_sells_at_highest_prices_available() {
        val book = OrderBook(PIZZA, COLA)
        val seller = Agent(PIZZA, 8.0)
        val buyer = Agent(COLA, 12.0)
        buyer.produce()
        seller.produce()

        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3.0 cola/pizza
        book.buy(1.0, 4.0, buyer) // buy 1 pizza for 4.0 cola/pizza
        book.sell(1.0, 4.0, seller) // sell 1 pizza for 4 cola

        assertEquals(7.0, seller.amount(PIZZA))
        assertEquals(4.0, seller.amount(COLA))

        assertEquals(1.0, buyer.amount(PIZZA))
        assertEquals(8.0, buyer.amount(COLA))

        assertEquals(listOf(Order(3.0, 1.0, buyer)), book.bids())
        assertEquals(emptyList<Order>(), book.asks())
    }

}