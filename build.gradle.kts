import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "ru.qiwi.devops"
version = System.getenv("APP_VERSION") ?: "0.0.0"

val kotlinVersion = "1.3.61"
val springBootVersion = "2.2.2.RELEASE"
val springVersion = "5.2.2.RELEASE"
val junitJupiterVersion = "5.6.1"
val logbackVersion = "5.3"
val kubernetesClientVersion = "8.0.0"
val jwtVersion = "3.10.2"
val okhttpVersion = "4.2.2"
val resilience4jVersion = "1.4.0"
val assertkVersion = "0.21"
val config4kVersion = "0.3.4"
val ktlintVersion = "9.2.1"

repositories {
    maven { setUrl("https://repo.maven.apache.org/maven2") }
    maven { setUrl("https://jcenter.bintray.com/") }
    maven { setUrl("https://maven.osmp.ru/nexus/content/groups/public/") }
}

plugins {
    val kotlinVersion = "1.3.61"
    val springVersion = "2.2.2.RELEASE"
    val ktlintVersion = "9.2.1"

    id("org.springframework.boot") version springVersion
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version ktlintVersion

    idea
}

apply(plugin = "io.spring.dependency-management")

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<Jar> {
    archiveName = "mission-control.jar"
}

springBoot {
    mainClassName = "ru.qiwi.devops.mission.control.DevopsMissionControlApplicationKt"
}

dependencies {
    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config:$springBootVersion")
    implementation("org.springframework.security:spring-security-ldap:$springVersion")

    // jwt
    implementation("com.auth0:java-jwt:$jwtVersion")

    // logging
    implementation("org.slf4j:slf4j-api")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackVersion")

    // kubernetes
    implementation("io.kubernetes:client-java:$kubernetesClientVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")

    // other
    implementation("io.projectreactor:reactor-core")
    implementation("io.github.resilience4j:resilience4j-all:$resilience4jVersion")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.unboundid:unboundid-ldapsdk")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testImplementation("io.github.config4k:config4k:$config4kVersion")
    testImplementation("io.projectreactor:reactor-test")
}
