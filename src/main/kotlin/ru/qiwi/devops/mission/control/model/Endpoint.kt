package ru.qiwi.devops.mission.control.model

data class Endpoint(
    val owner: EndpointOwner,
    val name: String,
    val address: EndpointAddress,
    val target: PortInfo
)

data class RawEndpoint(
    val owner: EndpointOwner,
    val name: String,
    val address: EndpointAddress,
    val targetPort: String
) {
    fun toEndpoint(targetMapper: (String) -> PortInfo): Endpoint {
        return Endpoint(
            owner = owner,
            name = name,
            address = address,
            target = targetMapper(targetPort)
        )
    }
}

sealed class EndpointAddress(
    val type: String
) {
    class NodePortEndpointAddress(val port: Int) : EndpointAddress("NODE_PORT")

    class HostEndpointAddress(val hostName: String, val port: Int) : EndpointAddress("HOST")

    class URLEndpointAddress(val url: String) : EndpointAddress("URL")
}

data class EndpointOwner(
    val type: EndpointOwnerType,
    val name: String
)

enum class EndpointOwnerType {
    POD,
    SERVICE,
    INGRESS
}