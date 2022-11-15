plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  alias(libs.plugins.pluginpublish)
  `maven-publish`
}

group = "io.github.portlek.smol-plugin-gradle"

repositories {
  maven("https://plugins.gradle.org/m2/")
}

dependencies {
  compileOnly(project(":smol"))
  compileOnly(kotlin("stdlib", "1.7.21"))
  compileOnly(libs.coroutines)
  compileOnly(libs.gson)
  compileOnly(libs.shadow)
}

tasks {
  compileKotlin {
    kotlinOptions {
      jvmTarget = "17"
      freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
    (options as StandardJavadocDocletOptions).tags("todo")
  }

  val javadocJar by creating(Jar::class) {
    dependsOn("javadoc")
    archiveClassifier.set("javadoc")
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    from(javadoc)
  }

  val sourcesJar by creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    from(sourceSets["main"].allSource)
  }

  build {
    dependsOn(jar)
    dependsOn(sourcesJar)
    dependsOn(javadocJar)
  }
}

publishing {
  publications {
    withType<MavenPublication> {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()

      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])
      pom {
        name.set(project.name)
        description.set("Java runtime dependency management.")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://mit-license.org/license.txt")
          }
        }
        developers {
          developer {
            id.set("portlek")
            name.set("Hasan Demirtaş")
            email.set("utsukushihito@outlook.com")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/portlek/smol.git")
          developerConnection.set("scm:git:ssh://github.com/portlek/smol.git")
          url.set("https://github.com/portlek/smol")
        }
      }
    }
  }
}

gradlePlugin {
  plugins {
    create("smol") {
      id = "io.github.portlek.smol-plugin-gradle"
      displayName = "Smol"
      description = "Java runtime dependency management."
      implementationClass = "io.github.portlek.smol.SmolPlugin"
    }
  }
}

pluginBundle {
  website = "https://github.com/portlek/smol"
  vcsUrl = "https://github.com/portlek/smol"
  tags = listOf("runtime dependency", "relocation")
  description = "Very easy to setup and downloads any public dependency at runtime!"
}
