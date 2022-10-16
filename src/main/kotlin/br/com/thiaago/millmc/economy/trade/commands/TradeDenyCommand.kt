package br.com.thiaago.millmc.economy.trade.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.trade.controller.TradeController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TradeDenyCommand(private val tradeController: TradeController) {

    @Command(name = "tradedeny", aliases = ["trocarecusar"], target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        val messagesConfig =
            MillMCEconomy.instance!!.configController!!.configs[MessagesConfig::class.java]!!.getConfig()!!

        if (context.argsCount() != 1) return

        val playerSender: Player = Bukkit.getPlayer(context.getArg(0)) ?: return

        if (!playerSender.isOnline)
            return

        if (!tradeController.playersWaitingTrade.containsKey(playerSender.name)) return

        tradeController.playersWaitingTrade.remove(player.name)

        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "TRADE_DENIED",
                player,
                emptyMap(),
                messagesConfig
            )
        )
    }

}