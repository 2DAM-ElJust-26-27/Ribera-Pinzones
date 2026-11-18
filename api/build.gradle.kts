plugins { application }
repositories { mavenCentral() }
dependencies {
    implementation("io.javalin:javalin:7.2.4")
    implementation("org.postgresql:postgresql:42.7.14")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")
}
java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }
tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
application {
    mainClass = "org.connectantribera.Main"
    applicationName = "ribera-api"
}
