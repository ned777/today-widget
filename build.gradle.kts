// The ROOT build file — declares which Gradle PLUGINS are available to use,
// without actually turning them on yet (`apply false`) — app/build.gradle.kts
// then opts into the specific plugins it actually needs.

plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}
