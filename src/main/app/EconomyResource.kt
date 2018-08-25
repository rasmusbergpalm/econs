package app

import core.Agent
import core.Auctioneer
import core.Product
import java.util.concurrent.ThreadLocalRandom
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import kotlin.math.pow


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class EconomyResource {


    @POST
    @Path("/trade")
    fun trade(inputs: Map<Product, Double>): TradeSummary {
        val r = ThreadLocalRandom.current()!!
        val agents = mutableListOf<Agent>()
        for ((product, mean) in inputs) {
            agents.addAll((1..500).map {
                Agent(mutableMapOf(product to 10.0.pow(r.nextGaussian() / 10.0 + Math.log10(mean))))
            })
        }

        val auctioneer = Auctioneer()

        val unitOfValue = Product.GOLD

        val initialPrices = Product.values().asList().minus(unitOfValue).map { it to 1.0 }.toMap()

        val prices = auctioneer.discoverPrices(agents, initialPrices, unitOfValue)

        val beforeAndAfter = agents.map { it to Agent(it.desiredAmounts(prices, unitOfValue).toMutableMap()) }

        val diffs = Product.values().map { it to 0.0 }.toMap().toMutableMap()

        val agentProperties = mutableListOf<Map<String, Double>>()
        for ((before, after) in beforeAndAfter) {
            val properties = mutableMapOf<String, Double>()
            properties["utility before"] = before.utility()
            properties["utility after"] = after.utility()
            for (p in Product.values()) {
                val a = before.inventory[p] ?: 0.0
                properties["$p before"] = a
                diffs[p] = diffs[p]!! + a
            }
            for (p in Product.values()) {
                val a = after.inventory[p] ?: 0.0
                properties["$p after"] = a
                diffs[p] = diffs[p]!! - a
            }

            val rounded = properties.mapValues { Math.round(it.value * 100.0) / 100.0 }
            agentProperties.add(rounded)
        }
        println(diffs)

        return TradeSummary(agentProperties, prices.mapValues { Math.round(it.value * 100.0) / 100.0 })
    }
}

data class TradeSummary(
        val agents: List<Map<String, Double>>,
        val prices: Map<Product, Double>
)