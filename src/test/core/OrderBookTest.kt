package test

import junit.framework.TestCase
import core.Agent
import core.Order
import core.OrderBook
import core.Product.CATTLE
import core.Product.GRAIN

class OrderBookTest : TestCase() {

    fun test_trade() {
        val book = OrderBook(GRAIN, CATTLE)
        val seller = Agent(mutableMapOf(GRAIN to 8.0))
        val buyer = Agent(mutableMapOf(CATTLE to 12.0))

        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3 cola
        book.sell(1.0, 3.0, seller) // sell 1 pizza for 3 cola

        assertEquals(7.0, seller.amount(GRAIN))
        assertEquals(3.0, seller.amount(CATTLE))

        assertEquals(1.0, buyer.amount(GRAIN))
        assertEquals(9.0, buyer.amount(CATTLE))

        assertEquals(emptyList<Order>(), book.bids())
        assertEquals(emptyList<Order>(), book.asks())
    }

    fun test_no_trade() {
        val book = OrderBook(GRAIN, CATTLE)
        val seller = Agent(mutableMapOf(GRAIN to 8.0))
        val buyer = Agent(mutableMapOf(CATTLE to 12.0))

        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3 cola
        book.sell(1.0, 4.0, seller) // sell 1 pizza for 4 cola

        assertEquals(8.0, seller.amount(GRAIN))
        assertEquals(0.0, seller.amount(CATTLE))

        assertEquals(0.0, buyer.amount(GRAIN))
        assertEquals(12.0, buyer.amount(CATTLE))

        assertEquals(listOf(Order(3.0, 1.0, buyer)), book.bids())
        assertEquals(listOf(Order(4.0, 1.0, seller)), book.asks())
    }

    fun test_partial_trade() {
        val book = OrderBook(GRAIN, CATTLE)
        val seller = Agent(mutableMapOf(GRAIN to 8.0))
        val buyer = Agent(mutableMapOf(CATTLE to 12.0))

        book.buy(2.5, 3.0, buyer) // buy 2.5 pizza for 3.0 cola/pizza
        book.sell(1.0, 3.0, seller) // sell 1 pizza for 3 cola

        assertEquals(7.0, seller.amount(GRAIN))
        assertEquals(3.0, seller.amount(CATTLE))

        assertEquals(1.0, buyer.amount(GRAIN))
        assertEquals(9.0, buyer.amount(CATTLE))

        assertEquals(listOf(Order(3.0, 1.5, buyer)), book.bids())
        assertEquals(emptyList<Order>(), book.asks())
    }

    fun test_buys_untill_he_runs_out_of_money_or_price_is_too_high() {
        val book = OrderBook(GRAIN, CATTLE)
        val seller = Agent(mutableMapOf(GRAIN to 8.0))
        val buyer = Agent(mutableMapOf(CATTLE to 12.0))

        book.sell(3.0, 1.0, seller) // sell 3 pizza for 3 cola. <3 bought>
        book.sell(3.0, 2.0, seller) // sell 3 pizza for 6 cola. <1.5 bought>
        book.sell(2.0, 4.0, seller) // sell 1 pizza for 4 cola
        book.buy(2.0, 3.0, buyer) // buy for 6 cola while price less than 3.0

        assertEquals(3.5, seller.amount(GRAIN))
        assertEquals(6.0, seller.amount(CATTLE))

        assertEquals(4.5, buyer.amount(GRAIN))
        assertEquals(6.0, buyer.amount(CATTLE))

        assertEquals(emptyList<Order>(), book.bids())
        assertEquals(listOf(Order(2.0, 1.5, seller), Order(4.0, 2.0, seller)), book.asks())
    }

    fun test_buyer_buys_at_lowest_prices_available() {
        val book = OrderBook(GRAIN, CATTLE)
        val seller = Agent(mutableMapOf(GRAIN to 8.0))
        val buyer = Agent(mutableMapOf(CATTLE to 12.0))

        book.sell(1.0, 3.0, seller) // sell 1 pizza for 3 cola
        book.sell(1.0, 4.0, seller) // sell 1 pizza for 4 cola
        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3.0 cola/pizza

        assertEquals(7.0, seller.amount(GRAIN))
        assertEquals(3.0, seller.amount(CATTLE))

        assertEquals(1.0, buyer.amount(GRAIN))
        assertEquals(9.0, buyer.amount(CATTLE))

        assertEquals(emptyList<Order>(), book.bids())
        assertEquals(listOf(Order(4.0, 1.0, seller)), book.asks())
    }

    fun test_seller_sells_at_highest_prices_available() {
        val book = OrderBook(GRAIN, CATTLE)
        val seller = Agent(mutableMapOf(GRAIN to 8.0))
        val buyer = Agent(mutableMapOf(CATTLE to 12.0))

        book.buy(1.0, 3.0, buyer) // buy 1 pizza for 3.0 cola/pizza
        book.buy(1.0, 4.0, buyer) // buy 1 pizza for 4.0 cola/pizza
        book.sell(1.0, 4.0, seller) // sell 1 pizza for 4 cola

        assertEquals(7.0, seller.amount(GRAIN))
        assertEquals(4.0, seller.amount(CATTLE))

        assertEquals(1.0, buyer.amount(GRAIN))
        assertEquals(8.0, buyer.amount(CATTLE))

        assertEquals(listOf(Order(3.0, 1.0, buyer)), book.bids())
        assertEquals(emptyList<Order>(), book.asks())
    }

}