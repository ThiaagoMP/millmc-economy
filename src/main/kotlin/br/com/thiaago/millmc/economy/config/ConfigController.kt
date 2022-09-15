package br.com.thiaago.millmc.economy.config

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.BaltopConfig
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig

class ConfigController(val configs: MutableMap<Class<*>, CustomConfig> = emptyMap<Class<*>, CustomConfig>().toMutableMap()) {

    fun load(plugin: MillMCEconomy): ConfigController {
        configs[MessagesConfig::class.java] = MessagesConfig().setup(plugin)
        configs[BaltopConfig::class.java] = BaltopConfig().setup(plugin)
        return this
    }

}