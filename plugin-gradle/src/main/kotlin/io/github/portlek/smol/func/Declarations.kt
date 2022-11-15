package io.github.portlek.smol.func

import io.github.portlek.smol.exceptions.ConfigurationNotFoundException
import io.github.portlek.smol.smolJarLib
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope

val Project.performCompileTimeResolution
  get() = findProperty("smol.default.resolution.compile_time")?.toString()?.toBoolean() ?: true

val Project.smolInjectToIsolated
  get() = findProperty("smol.default.isolated.inject")?.toString()?.toBoolean() ?: true

fun Project.createConfig(configName: String, vararg extends: String): Configuration {
  val config =
      extends.map {
        configurations.findByName(it)
            ?: throw ConfigurationNotFoundException("Could not find `$extends` configuration!")
      }
  val smolConfig = configurations.create(configName)
  config.forEach { it.extendsFrom(smolConfig) }
  smolConfig.isTransitive = true
  return smolConfig
}

fun DependencyHandlerScope.smol(version: String = "+"): String = smolJarLib(version)

fun DependencyHandler.smol(version: String = "+"): String = smolJarLib(version)
