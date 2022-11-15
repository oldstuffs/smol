plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  alias(libs.plugins.pluginpublish)
  alias(libs.plugins.shadow)
  `maven-publish`
}

repositories {
  maven("https://plugins.gradle.org/m2/")
}

dependencies {
  implementation(kotlin("stdlib", "1.7.21"))
  implementation(libs.coroutines)
}
