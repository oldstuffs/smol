# Smol
[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Update Snapshot](https://github.com/portlek/smol/actions/workflows/snapshot.yml/badge.svg)](https://github.com/portlek/smol/actions/workflows/snapshot.yml)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.portlek/smol?label=maven-central&server=https%3A%2F%2Foss.sonatype.org%2F)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.portlek/smol?label=maven-central&server=https%3A%2F%2Foss.sonatype.org)
## How to Use (Developers)
```groovy
plugins {
  id "java"
  id "java-library"
  id "com.github.johnrengelman.shadow" version "7.1.2"
  id "io.github.portlek.smol-plugin-gradle" version "VERSION"
}

dependencies {
  implementation "io.github.portlek:smol:VERSION"
  // Or
  implementation smolJar("VERSION")
  // Or
  implementation smolJar()

  smol "anypackage:anyartifact:anyversion" // extends compileOnly
  smolApi "anypackage:anyartifact:anyversion" // extends compileOnlyApi
}
```