package br.com.thiaago.millmc.economy.loader

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.basic.baltop.inventory.BaltopInventory
import br.com.thiaago.millmc.economy.config.impl.BaltopConfig
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.MarketCategoriesInventory
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.MarketItemsInventory
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.manage.MarketMyItemsExpiredInventory
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.manage.MarketMyItemsInventory
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.negotiation.MarketNegotiationItemInventory
import me.saiintbrisson.minecraft.ViewFrame

class ViewFrameLoader {

    companion object {

        fun load(plugin: MillMCEconomy): ViewFrame {
            return ViewFrame.of(
                plugin, BaltopInventory(
                    plugin.baltopController!!,
                    plugin.configController.configs[BaltopConfig::class.java]!!.getConfig()!!,
                    plugin.configController.configs[MessagesConfig::class.java]!!.getConfig()!!
                ),
                MarketCategoriesInventory(plugin.marketController!!),
                MarketItemsInventory(),
                MarketMyItemsInventory(plugin.marketController!!),
                MarketMyItemsExpiredInventory(plugin.marketController!!),
                MarketNegotiationItemInventory(plugin.accountController!!)
            ).register()
        }
    }

}