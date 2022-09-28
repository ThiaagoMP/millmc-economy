package br.com.thiaago.millmc.economy.marketplace

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.marketplace.spigot.commands.MarketAnnounceCommand
import br.com.thiaago.millmc.economy.marketplace.spigot.commands.MarketCommand
import me.saiintbrisson.bukkit.command.BukkitFrame

class MarketLoader {

    companion object {
        fun load(bukkitFrame: BukkitFrame) {
            bukkitFrame.registerCommands(
                MarketCommand(),
                MarketAnnounceCommand(MillMCEconomy.instance!!.marketController!!)
            )
        }
    }

}