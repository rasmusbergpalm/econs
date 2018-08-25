import app.EconConfiguration
import app.EconomyResource
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class EconApplication : Application<EconConfiguration>() {

    override fun getName(): String {
        return "econs"
    }

    override fun initialize(bootstrap: Bootstrap<EconConfiguration>) {
        bootstrap.addBundle(AssetsBundle("/assets/", "/app", "index.html"))
    }

    override fun run(configuration: EconConfiguration, environment: Environment) {

        environment.jersey().register(EconomyResource())
        environment.objectMapper.registerModule(KotlinModule())
        environment.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
    }

}

fun main(args: Array<String>) {
    EconApplication().run(*args)
}