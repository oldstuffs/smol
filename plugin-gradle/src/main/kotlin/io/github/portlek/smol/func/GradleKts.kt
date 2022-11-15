package io.github.portlek.smol.func

import io.github.portlek.smol.SMOL_API_CONFIGURATION_NAME
import io.github.portlek.smol.SMOL_CONFIGURATION_NAME
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider

fun DependencyHandler.smol(
    dependencyNotation: Provider<*>,
    dependencyOptions: Action<in ExternalModuleDependency>
) {
  addProvider(SMOL_CONFIGURATION_NAME, dependencyNotation, dependencyOptions)
}

fun DependencyHandler.smolApi(
    dependencyNotation: Provider<*>,
    dependencyOptions: Action<ExternalModuleDependency>
) {
  addProvider(SMOL_API_CONFIGURATION_NAME, dependencyNotation, dependencyOptions)
}

fun DependencyHandler.smol(dependencyNotation: Any): Dependency? =
    add(SMOL_CONFIGURATION_NAME, dependencyNotation)

fun DependencyHandler.smolApi(dependencyNotation: Any): Dependency? =
    add(SMOL_API_CONFIGURATION_NAME, dependencyNotation)
