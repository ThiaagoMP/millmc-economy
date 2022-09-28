package br.com.thiaago.millmc.economy.marketplace.data.dao

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.market.MarketQuantitiesPermissionsConfig
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketPermissions

class MarketQuantitiesDao {

    companion object {
        fun load(): List<MarketPermissions> {
            val list = emptyList<MarketPermissions>().toMutableList()
            val config =
                MillMCEconomy.instance!!.configController.configs[MarketQuantitiesPermissionsConfig::class.java]!!.getConfig()!!

            val section = config.getConfigurationSection("QUANTITIES")
            section.getKeys(false).forEach {
                list.add(MarketPermissions(section.getString("$it.PERMISSION"), section.getInt("$it.QUANTITY")))
            }
            list.sort()
            return list
        }
    }

}