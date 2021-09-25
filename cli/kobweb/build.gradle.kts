plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "com.varabyte.kobweb.cli"
version = libs.versions.kobweb.get()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.cli)
    implementation(libs.konsole)
    implementation(libs.jgit)
    implementation(libs.freemarker)
    implementation(libs.kaml)
    implementation(project(":common:kobweb"))
}

application {
    applicationDefaultJvmArgs = listOf("-Dkobweb.version=${version}")
    mainClass.set("MainKt")
}

// Avoid ambiguity / add clarity in generated artifacts
tasks.jar {
    archiveFileName.set("kobweb-cli.jar")
}