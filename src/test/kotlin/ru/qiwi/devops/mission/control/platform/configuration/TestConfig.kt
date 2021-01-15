package ru.qiwi.devops.mission.control.platform.configuration

object TestConfig {
    val source = ConfigurationSource.read("tests")

    inline fun <reified T> load(path: String): T {
        return source.load("tests.$path")
    }

    val applicationName = load<String>("name")

    val environment = load<String>("environment")

    val properties = load<Map<String, String>>("properties")
}
