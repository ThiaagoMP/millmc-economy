package br.com.thiaago.millmc.economy.config

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.BaltopConfig
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.config.impl.market.*

class ConfigController(val configs: MutableMap<Class<*>, CustomConfig> = emptyMap<Class<*>, CustomConfig>().toMutableMap()) {

    fun load(plugin: MillMCEconomy): ConfigController {
        configs[MessagesConfig::class.java] = MessagesConfig().setup(plugin)
        configs[BaltopConfig::class.java] = BaltopConfig().setup(plugin)
        configs[MarketItemsInventoryConfig::class.java] = MarketItemsInventoryConfig().setup(plugin)
        configs[MarketMyItemsInventoryConfig::class.java] = MarketMyItemsInventoryConfig().setup(plugin)
        configs[MarketMyItemsExpiredInventoryConfig::class.java] = MarketMyItemsExpiredInventoryConfig().setup(plugin)
        configs[MarketCategoriesInventoryConfig::class.java] = MarketCategoriesInventoryConfig().setup(plugin)
        configs[MarketNegotiationInventoryConfig::class.java] = MarketNegotiationInventoryConfig().setup(plugin)
        configs[MarketQuantitiesPermissionsConfig::class.java] = MarketQuantitiesPermissionsConfig().setup(plugin)
        return this
    }

}