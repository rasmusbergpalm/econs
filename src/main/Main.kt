package main

/**
 * TODO
 */
fun main(args: Array<String>) {
    val a1 = Agent(Product.PIZZA, 12.0)
    val a2 = Agent(Product.COLA, 12.0)
    val agents = listOf(a1, a2)
    for (t in (0..5)) {
        println("time: $t")
        for ((i, agent) in agents.withIndex()) {
            agent.tick()
            println("agent: $i utility: ${agent.utility()}")
        }

    }
}