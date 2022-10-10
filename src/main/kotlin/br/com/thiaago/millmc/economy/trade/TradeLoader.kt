package br.com.thiaago.millmc.economy.trade

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.trade.commands.TradeAcceptCommand
import br.com.thiaago.millmc.economy.trade.commands.TradeCommand
import br.com.thiaago.millmc.economy.trade.commands.TradeDenyCommand
import br.com.thiaago.millmc.economy.trade.listeners.TradeListeners
import me.saiintbrisson.bukkit.command.BukkitFrame
import org.bukkit.Bukkit

class TradeLoader {

    companion object {
        fun load(bukkitFrame: BukkitFrame, plugin: MillMCEconomy) {
            val messagesConfig = plugin.configController!!.configs[MessagesConfig::class.java]!!.getConfig()!!
            bukkitFrame.registerCommands(
                TradeCommand(
                    messagesConfig,
                    plugin.tradeController!!
                ),
                TradeAcceptCommand(
                    messagesConfig,
                    plugin.tradeController!!
                ),
                TradeDenyCommand(
                    messagesConfig,
                    plugin.tradeController!!
                )
            )
            Bukkit.getPluginManager().registerEvents(TradeListeners(plugin.tradeController!!), plugin)
        }
    }

}