package br.com.thiaago.millmc.economy.trade.commands

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.TradeConfig
import br.com.thiaago.millmc.economy.trade.controller.TradeController
import br.com.thiaago.millmc.economy.trade.inventory.TradeInventory
import br.com.thiaago.millmc.economy.trade.model.Trade
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TradeAcceptCommand(private val tradeController: TradeController) {

    @Command(
        name = "tradeaccept",
        aliases = ["trocaaceitar"],
        target = CommandTarget.PLAYER
    )
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        if (context.argsCount() != 1) return

        val playerSender: Player = Bukkit.getPlayer(context.getArg(0)) ?: return

        if (!playerSender.isOnline) return

        if (!tradeController.playersWaitingTrade.containsKey(playerSender.name)) return

        tradeController.playersWaitingTrade.remove(playerSender.name)
        tradeController.trades.add(Trade(playerSender, player))

        TradeInventory(
            playerSender, player,
            MillMCEconomy.instance!!.configController!!.configs[TradeConfig::class.java]!!.getConfig()!!
        )
    }
}