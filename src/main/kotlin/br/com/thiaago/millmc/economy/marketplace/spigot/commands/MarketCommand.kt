package br.com.thiaago.millmc.economy.marketplace.spigot.commands

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.MarketCategoriesInventory
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MarketCommand {

    @Command(name = "market", aliases = ["mercado"], target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        MillMCEconomy.instance!!.viewFrame!!.open(MarketCategoriesInventory::class.java, player)
    }

}