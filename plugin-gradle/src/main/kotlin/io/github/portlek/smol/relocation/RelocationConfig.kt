package io.github.portlek.smol.relocation

class RelocationConfig {
  internal val inclusions = mutableListOf<String>()
  internal val exclusions = mutableListOf<String>()

  fun include(vararg pattern: String): RelocationConfig {
    inclusions.addAll(pattern)
    return this
  }

  fun exclude(vararg pattern: String): RelocationConfig {
    exclusions.addAll(pattern)
    return this
  }
}
