plugins {
    java
    id("org.springframework.boot")
    id("com.google.cloud.tools.jib")
}

jib {
    from {
        image = "eclipse-temurin:25-jre-alpine"
    }
    to {
        image = "gudrb963/runmarket-pacer-batch"
        tags = setOf("latest")
    }
}

val jsoup = "1.18.3"

dependencies {
    implementation(project(":application"))
    runtimeOnly(project(":infrastructure"))
    runtimeOnly(project(":event-bus"))

    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.jsoup:jsoup:$jsoup")
}
