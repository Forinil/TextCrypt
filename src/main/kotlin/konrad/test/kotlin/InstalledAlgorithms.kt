package konrad.test.kotlin

import java.security.Security

/**
 * Stworzone przez Konrad Botor dnia 2016-12-08.
 */
fun main(args: Array<String>) {
    val providers =  Security.getProviders()
    providers.forEach {
        provider -> provider.services.forEach {
            service -> println("Provider: $provider, service: $service, algorithm: ${service.algorithm}")
        }
    }
}