package com.github.rungeun.kcj.kotlincodejudge

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

data class Config(
    val inText: String,
    val outText: String,
    val answerText: String,
    val cerrText: String
)

object ConfigManager {
    private val mapper = jacksonObjectMapper()

    fun saveConfig(config: Config, filePath: String) {
        mapper.writeValue(File(filePath), config)
    }

    fun loadConfig(filePath: String): Config {
        return mapper.readValue(File(filePath))
    }
}
