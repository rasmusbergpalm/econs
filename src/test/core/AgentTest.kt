import junit.framework.TestCase
import core.Agent
import core.Product

class AgentTest : TestCase() {

    fun test_given_optimal_endowment_no_changes() {
        val agent = Agent(mutableMapOf(
                Product.GRAIN to 12.0,
                Product.CATTLE to 12.0
        ))

        val prices = mapOf(
                Product.GRAIN to 1.0,
                Product.CATTLE to 1.0
        )

        val actual = agent.desiredAmounts(prices, Product.CATTLE)
        val expected = mapOf(
                Product.GRAIN to 12.0,
                Product.CATTLE to 12.0
        )

        assertEquals(expected, actual)
    }

    fun test_at_1to1_prices_wants_to_trade_6_for_6() {
        // https://www.wolframalpha.com/input/?i=max+sqrt(x)%2Bsqrt(y)+given+x%2By+%3D+12
        val agent = Agent(mutableMapOf(
                Product.GRAIN to 12.0,
                Product.CATTLE to 0.0
        ))

        val prices = mapOf(
                Product.GRAIN to 1.0,
                Product.CATTLE to 1.0
        )

        val actual = agent.desiredAmounts(prices, Product.CATTLE)
        val expected = mapOf(
                Product.GRAIN to 6.0,
                Product.CATTLE to 6.0
        )

        assertEquals(expected, actual)
    }

    fun test_at_2to1_prices_wants_to_trade_8_for_16() {
        // https://www.wolframalpha.com/input/?i=max+sqrt(x)%2Bsqrt(y)+given+x*2%2By+%3D+24
        val agent = Agent(mutableMapOf(
                Product.GRAIN to 12.0,
                Product.CATTLE to 0.0
        ))

        val prices = mapOf(
                Product.GRAIN to 2.0,
                Product.CATTLE to 1.0
        )

        val actual = agent.desiredAmounts(prices, Product.CATTLE)
        val expected = mapOf(
                Product.GRAIN to 4.0,
                Product.CATTLE to 16.0
        )

        assertEquals(expected, actual)
    }

    fun test_at_1to2_prices_wants_to_trade_4_for_2() {
        // https://www.wolframalpha.com/input/?i=max+sqrt(x)%2Bsqrt(y)+given+x*0.5%2By+%3D+6
        val agent = Agent(mutableMapOf(
                Product.GRAIN to 12.0,
                Product.CATTLE to 0.0
        ))

        val prices = mapOf(
                Product.GRAIN to 0.5,
                Product.CATTLE to 1.0
        )

        val actual = agent.desiredAmounts(prices, Product.CATTLE)
        val expected = mapOf(
                Product.GRAIN to 8.0,
                Product.CATTLE to 2.0
        )

        assertEquals(expected, actual)
    }

    fun test_3_products() {
        //https://www.wolframalpha.com/input/?i=max+sqrt(x)%2Bsqrt(y)%2Bsqrt(z)+given+x*4%2By*2%2Bz+%3D+12
        val agent = Agent(mutableMapOf(
                Product.GRAIN to 3.0,
                Product.CATTLE to 0.0,
                Product.WOOL to 0.0
        ))

        val prices = mapOf(
                Product.GRAIN to 4.0,
                Product.CATTLE to 2.0,
                Product.WOOL to 1.0
        )

        val actual = agent.desiredAmounts(prices, Product.WOOL)
        val expected = mapOf(
                Product.GRAIN to 3.0 / 7.0,
                Product.CATTLE to 12.0 / 7.0,
                Product.WOOL to 48.0 / 7.0
        )

        assertEquals(expected, actual)
    }
}