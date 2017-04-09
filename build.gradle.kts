buildscript {

    repositories {
        gradleScriptKotlin()
        mavenCentral()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

plugins {
    application
}

apply {
    plugin("kotlin")
}

repositories {
    gradleScriptKotlin()
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile(kotlinModule("reflect"))

    // TODO decide if we want to use it or not
    compile("org.reflections:reflections:0.9.11")

    testCompile("io.kotlintest:kotlintest:1.3.4")
    testCompile("org.assertj:assertj-core:3.6.2")
}

fun KotlinDependencyHandler.testCombo() {
    testCompile("io.kotlintest:kotlintest:1.3.4")
    testCompile("org.assertj:assertj-core:3.6.2")
}

