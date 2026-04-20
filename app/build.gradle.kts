plugins {
    application
    alias(libs.plugins.shadow)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "io.github.luciegrillo.civitas.app.CivitasApplication"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.jackson.databind)
    implementation(libs.picocli)
    implementation(libs.xchart)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.shadowJar {
    archiveClassifier = ""
    mergeServiceFiles()
}

tasks.jar {
    archiveClassifier = "plain"
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
