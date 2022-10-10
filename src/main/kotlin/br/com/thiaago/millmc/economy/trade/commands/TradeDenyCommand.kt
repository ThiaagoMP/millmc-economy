package br.com.thiaago.millmc.economy.trade.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.trade.data.controller.TradeController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class TradeDenyCommand(private val messagesConfig: FileConfiguration, private val tradeController: TradeController) {

    @Command(name = "tradedeny", target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        if (context.argsCount() != 1) return

        val targetPlayer = Bukkit.getPlayer(context.getArg(0))
        if (targetPlayer == null || !targetPlayer.isOnline || !tradeController.tradesRequests.containsKey(targetPlayer)
            || tradeController.tradesRequests[targetPlayer] != player
        ) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "PLAYER_NOT_FOUND_TRADE",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            )
            tradeController.tradesRequests.remove(targetPlayer)
            return
        }
        tradeController.tradesRequests.remove(targetPlayer)
        player.sendMessage(LanguageAPI.getTranslatedMessage("TRADE_DENIED", player, emptyMap(), messagesConfig))
    }

}