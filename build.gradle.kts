import org.gradle.api.plugins.*
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.file.pattern.PatternMatcherFactory.compile
import org.gradle.script.lang.kotlin.*

private val kotlinVersion = "1.2.21"

plugins {
    `java-library`
}

version = "0.9.0"

repositories.jcenter()

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    testImplementation("junit:junit:4.12")
}

buildscript {
    repositories.mavenCentral()
    dependencies.classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
