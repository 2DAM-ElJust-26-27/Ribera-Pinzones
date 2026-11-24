
plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // PostgreSQL JDBC
    implementation("org.postgresql:postgresql:42.7.7")

    // Pruebas
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Guava (puedes conservarla)
    implementation(libs.guava)

    implementation("org.apache.commons:commons-csv:1.14.1")
    implementation("tools.jackson.core:jackson-databind:3.0.3")
    implementation("org.postgresql:postgresql:42.7.7")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.pinzones.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

