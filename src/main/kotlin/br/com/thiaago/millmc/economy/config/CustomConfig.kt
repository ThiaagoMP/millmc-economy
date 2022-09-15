package br.com.thiaago.millmc.economy.config

import br.com.thiaago.millmc.economy.MillMCEconomy
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

interface CustomConfig {

    fun setup(plugin: MillMCEconomy): CustomConfig

    fun getConfigName(): String?

    fun getConfig(): FileConfiguration?

    fun getFile(): File?

    fun save()

}