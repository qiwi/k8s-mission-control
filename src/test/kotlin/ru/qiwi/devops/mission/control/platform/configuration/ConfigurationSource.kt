package ru.qiwi.devops.mission.control.platform.configuration

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ConfigurationSource(
    val config: Config
) {
    inline fun <reified T> load(path: String): T {
        return config.extract(path)
    }

    companion object {
        fun read(name: String): ConfigurationSource {
            val rootConfig = ConfigFactory.load(name)
            val environment = rootConfig.extract<String>("tests.environment")
            val configDirs = rootConfig.getConfigDirs()
            val configNames = rootConfig.getConfigNames()

            val resourcesConfig = ConfigFactory.load("$name.$environment")
                .withFallback(rootConfig)

            val filesConfigs = createFileNames(configDirs, configNames, environment)
                .reversed()
                .map { ConfigFactory.parseFileAnySyntax(it) }

            val config = (filesConfigs + resourcesConfig).reduce { a, b -> a.withFallback(b) }

            return ConfigurationSource(config)
        }
    }
}

private fun createFileNames(dirs: Iterable<Path>, configNames: Iterable<String>, environment: String): Iterable<File> {
    return configNames
        .flatMap { name -> listOf(name, "$name-$environment") }
        .flatMap { name -> dirs.map { dir -> dir.resolve(name) } }
        .filter { file -> Files.exists(file) }
        .map { file -> file.toFile() }
}

private fun Config.getConfigDirs(): Iterable<Path> {
    return this.getString("tests.configDirs")
        ?.split(",")
        ?.filter { it.isNotBlank() }
        ?.map { Paths.get(it) }
        ?: listOf(Paths.get("."))
}

private fun Config.getConfigNames(): Iterable<String> {
    return this.getString("tests.configNames")?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
}
