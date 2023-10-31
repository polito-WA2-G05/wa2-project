import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.3.1"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
}

jib{
    to{
        image="gcr.io/g5project-385811/server"
    }
}

group = "it.polito.wa2.g05"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation ("com.auth0:java-jwt:3.18.2")
    implementation("org.keycloak:keycloak-spring-boot-starter:21.0.1")
    implementation("org.keycloak:keycloak-admin-client:21.0.1")

    // using new @Observed on class and enaabled @ObservedAspect
    implementation ("org.springframework.boot:spring-boot-starter-aop")
    // enabled endpoint and expose metrics
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("io.micrometer:micrometer-registry-prometheus")
    // handleing lifecycle of a span
    implementation ("io.micrometer:micrometer-tracing-bridge-brave")
    // send span and trace data
    // endpoint is default to "http://locahost:9411/api/v2/spans" by actuator
    // we could setting by management.zipkin.tracing.endpoint
    implementation ("io.zipkin.reporter2:zipkin-reporter-brave")
    // send logs by log Appender through URL
    implementation ("com.github.loki4j:loki-logback-appender:1.4.0-rc2")
    implementation("org.codehaus.janino:janino")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation ("org.testcontainers:junit-jupiter:1.16.3")
    testImplementation("org.testcontainers:postgresql:1.16.3")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.16.3")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
