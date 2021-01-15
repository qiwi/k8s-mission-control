package ru.qiwi.devops.mission.control.model

class PortsMap<T>(
    private val byNames: Map<String, T>,
    private val byPorts: Map<Int, T>
) where T : AbstractPort {
    companion object {
        fun <T> create(ports: Iterable<T>): PortsMap<T> where T : AbstractPort {
            return PortsMap(
                ports.associateBy { it.name },
                ports.associateBy { it.port }
            )
        }
    }

    fun findOne(port: String): T? {
        return port.toIntOrNull()
            ?.let { num -> byPorts[num] }
            ?: byNames[port]
    }
}