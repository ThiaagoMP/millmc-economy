package br.com.thiaago.millmc.economy.trade.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.trade.data.controller.TradeController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class TradeCommand(private val messagesConfig: FileConfiguration, private val tradeController: TradeController) {

    @Command(name = "trade", aliases = ["troca", "trocar"], target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        if (context.argsCount() != 1) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "COMMAND_INCORRECT_TRADE",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            )
            return
        }

        if (tradeController.tradesRequests.containsKey(player)) tradeController.tradesRequests.remove(player)

        val targetPlayer = Bukkit.getPlayer(context.getArg(0))
        if (targetPlayer == player) {
            player.playSound(player.location, Sound.CAT_MEOW, 4f, 1f)
            return
        }
        if (targetPlayer == null || !targetPlayer.isOnline) {
            player.sendMessage(LanguageAPI.getTranslatedMessage("PLAYER_NOT_FOUND", player, emptyMap(), messagesConfig))
            return
        }

        player.sendMessage(LanguageAPI.getTranslatedMessage("SENT_TRADE", player, emptyMap(), messagesConfig))
        targetPlayer.sendMessage(LanguageAPI.getTranslatedMessage("RECEIVED_TRADE", player, emptyMap(), messagesConfig))

        val acceptJson =
            ComponentBuilder(
                LanguageAPI.getTranslatedMessage(
                    "ACCEPT_TRADE_JSON",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            ).event(
                ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tradeaccept " + player.name)
            ).create()[0]
        val denyJson =
            ComponentBuilder(
                LanguageAPI.getTranslatedMessage(
                    "DENY_TRADE_JSON",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            ).event(
                ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tradedeny " + player.name)
            ).create()[0]

        targetPlayer.spigot().sendMessage(acceptJson, denyJson)
        tradeController.tradesRequests[player] = targetPlayer

        object : BukkitRunnable() {
            override fun run() {
                tradeController.tradesRequests.remove(player)
            }
        }.runTaskLater(MillMCEconomy.instance, 20 * 60)
    }

}