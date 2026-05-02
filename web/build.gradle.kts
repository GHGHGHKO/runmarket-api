plugins {
    java
}

val jjwt = "0.13.0"

dependencies {
    implementation(project(":application"))
    runtimeOnly(project(":infrastructure"))
    runtimeOnly(project(":event-bus"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("io.jsonwebtoken:jjwt-api:$jjwt")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwt")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwt")
}
