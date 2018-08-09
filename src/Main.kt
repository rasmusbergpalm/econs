/**
 * TODO
 */
fun main(args: Array<String>) {
    val agents = (0..10).map { Agent() }
    for (t in (0..5)) {
        println("time: $t")
        for ((i, agent) in agents.withIndex()) {
            agent.tick()
            println("agent: $i utility: ${agent.utility()}")
        }

    }
}