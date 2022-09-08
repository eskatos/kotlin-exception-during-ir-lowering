plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

afterEvaluate {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            apiVersion = "1.5"
            languageVersion = "1.5"
//            freeCompilerArgs += "-Xuse-old-backend" // works if using the old backend
        }
    }
}
