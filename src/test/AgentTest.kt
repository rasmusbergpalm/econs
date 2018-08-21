import junit.framework.TestCase
import main.Agent
import main.Product

class AgentTest : TestCase() {

    fun test_given_optimal_endowment_no_changes() {
        val agent = Agent(mutableMapOf(
                Product.PIZZA to 12.0,
                Product.COLA to 12.0
        ))

        val prices = mapOf(
                Product.PIZZA to 1.0,
                Product.COLA to 1.0
        )

        val actual = agent.computeAmounts(prices)
        val expected = mapOf(
                Product.PIZZA to 12.0,
                Product.COLA to 12.0
        )

        assertEquals(expected, actual)
    }

    fun test_at_1to1_prices_wants_to_trade_6_for_6() {
        // https://www.wolframalpha.com/input/?i=max+sqrt(x)%2Bsqrt(y)+given+x%2By+%3D+12
        val agent = Agent(mutableMapOf(
                Product.PIZZA to 12.0,
                Product.COLA to 0.0
        ))

        val prices = mapOf(
                Product.PIZZA to 1.0,
                Product.COLA to 1.0
        )

        val actual = agent.computeAmounts(prices)
        val expected = mapOf(
                Product.PIZZA to 6.0,
                Product.COLA to 6.0
        )

        assertEquals(expected, actual)
    }

    fun test_at_2to1_prices_wants_to_trade_8_for_16() {
        // https://www.wolframalpha.com/input/?i=max+sqrt(x)%2Bsqrt(y)+given+x*2%2By+%3D+24
        val agent = Agent(mutableMapOf(
                Product.PIZZA to 12.0,
                Product.COLA to 0.0
        ))

        val prices = mapOf(
                Product.PIZZA to 2.0,
                Product.COLA to 1.0
        )

        val actual = agent.computeAmounts(prices)
        val expected = mapOf(
                Product.PIZZA to 4.0,
                Product.COLA to 16.0
        )

        assertEquals(expected, actual)
    }

    fun test_at_1to2_prices_wants_to_trade_4_for_2() {
        // https://www.wolframalpha.com/input/?i=max+sqrt(x)%2Bsqrt(y)+given+x*0.5%2By+%3D+6
        val agent = Agent(mutableMapOf(
                Product.PIZZA to 12.0,
                Product.COLA to 0.0
        ))

        val prices = mapOf(
                Product.PIZZA to 0.5,
                Product.COLA to 1.0
        )

        val actual = agent.computeAmounts(prices)
        val expected = mapOf(
                Product.PIZZA to 8.0,
                Product.COLA to 2.0
        )

        assertEquals(expected, actual)
    }
}