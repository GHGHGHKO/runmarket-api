plugins {
    `java-library`
}

dependencies {
    api(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework:spring-tx")
}
