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
    archiveBaseName = "civitas-lab"
    archiveClassifier = ""
    mergeServiceFiles()
}

tasks.jar {
    archiveBaseName = "civitas-lab"
    archiveClassifier = "plain"
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Title" to "Civitas Lab",
            "Implementation-Version" to project.version
        )
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    systemProperty("civitas.repoRoot", rootProject.projectDir.absolutePath)
}
