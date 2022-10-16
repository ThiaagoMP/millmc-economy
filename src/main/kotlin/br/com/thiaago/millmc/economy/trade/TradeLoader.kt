package br.com.thiaago.millmc.economy.trade

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.trade.commands.TradeAcceptCommand
import br.com.thiaago.millmc.economy.trade.commands.TradeCommand
import br.com.thiaago.millmc.economy.trade.commands.TradeDenyCommand
import br.com.thiaago.millmc.economy.trade.inventory.TradeInventoryListener
import me.saiintbrisson.bukkit.command.BukkitFrame
import org.bukkit.Bukkit

object TradeLoader {
    fun load(plugin: MillMCEconomy, bukkitFrame: BukkitFrame) {
        Bukkit.getPluginManager().registerEvents(
            TradeInventoryListener(
                plugin.tradeController!!,
                MillMCEconomy.instance!!.configController!!.configs[MessagesConfig::class.java]!!.getConfig()!!
            ), plugin
        )

        bukkitFrame.registerCommands(
            TradeCommand(plugin.tradeController!!),
            TradeAcceptCommand(plugin.tradeController!!),
            TradeDenyCommand(plugin.tradeController!!)
        )
    }
}