plugins {
  java
  `java-library`
  `maven-publish`
  signing
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks {
  compileJava {
    options.encoding = Charsets.UTF_8.name()
  }

  jar {
    archiveClassifier.set("")
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
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

val signRequired = !rootProject.property("dev").toString().toBoolean()

publishing {
  publications {
    val publication = create<MavenPublication>("mavenJava") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()

      from(components["java"])
      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])
      pom {
        name.set(project.name)
        description.set("Java runtime dependency management.")
        url.set("https://github.com/portlek/smol/")
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

    signing {
      isRequired = signRequired
      if (isRequired) {
        useGpgCmd()
        sign(publication)
      }
    }
  }
}
