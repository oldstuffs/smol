import com.diffplug.spotless.LineEnding

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.nexus)
}

group = "io.github.portlek"

repositories {
    mavenCentral()
}

val spotlessApply = rootProject.property("spotless.apply").toString().toBoolean()

if (spotlessApply) {
    spotless {
        lineEndings = LineEnding.UNIX

        format("encoding") {
            target("*.*")
            encoding("UTF-8")
        }

        java {
            target("**/src/**/java/**/*.java")
            importOrder()
            removeUnusedImports()
            endWithNewline()
            indentWithSpaces(2)
            trimTrailingWhitespace()
            prettier(
                mapOf(
                    "prettier" to "2.7.1",
                    "prettier-plugin-java" to "1.6.2"
                )
            ).config(
                mapOf(
                    "parser" to "java",
                    "tabWidth" to 2,
                    "useTabs" to false
                )
            )
        }
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}
