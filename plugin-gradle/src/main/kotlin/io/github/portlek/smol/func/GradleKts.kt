package io.github.portlek.smol.func

import io.github.portlek.smol.SMOL_API_CONFIGURATION_NAME
import io.github.portlek.smol.SMOL_CONFIGURATION_NAME
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.internal.Cast

fun DependencyHandler.smol(
    dependencyNotation: String,
    dependencyOptions: Action<ExternalModuleDependency>
): ExternalModuleDependency? {
  return withOptions(SMOL_CONFIGURATION_NAME, dependencyNotation, dependencyOptions)
}

fun DependencyHandler.smolApi(
    dependencyNotation: String,
    dependencyOptions: Action<ExternalModuleDependency>
): ExternalModuleDependency? {
  return withOptions(SMOL_API_CONFIGURATION_NAME, dependencyNotation, dependencyOptions)
}

fun DependencyHandler.smol(dependencyNotation: Any): Dependency? =
    add(SMOL_CONFIGURATION_NAME, dependencyNotation)

fun DependencyHandler.smolApi(dependencyNotation: Any): Dependency? =
    add(SMOL_API_CONFIGURATION_NAME, dependencyNotation)

private fun DependencyHandler.withOptions(
    configuration: String,
    dependencyNotation: String,
    dependencyConfiguration: Action<ExternalModuleDependency>
): ExternalModuleDependency? = run {
  Cast.uncheckedCast<ExternalModuleDependency>(create(dependencyNotation)).also { dependency ->
    if (dependency == null) return@run null
    dependencyConfiguration.execute(dependency)
    add(configuration, dependency)
  }
}
