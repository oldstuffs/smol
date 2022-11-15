package io.github.portlek.smol.tasks

import io.github.portlek.smol.relocation.RelocationRule
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.CacheableTask

@CacheableTask
class SmolJar
@Inject
constructor(private val smolConfig: Configuration, private val smolApiConfig: Configuration) :
    DefaultTask() {

  private val relocations = mutableSetOf<RelocationRule>()

  internal fun relocations() = this.relocations
}
