package br.com.thiaago.millmc.economy.trade.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.trade.controller.TradeController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class TradeCommand(private val tradeController: TradeController) {

    @Command(
        name = "trade",
        aliases = ["troca"],
        target = CommandTarget.PLAYER
    )
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        val messagesConfig =
            MillMCEconomy.instance!!.configController!!.configs[MessagesConfig::class.java]!!.getConfig()!!

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

        tradeController.playersWaitingTrade.remove(player.name)

        val playerReceived: Player = Bukkit.getPlayer(context.getArg(0)) ?: return

        if (!playerReceived.isOnline) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "PLAYER_NOT_FOUND_TRADE",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            )
            return
        }

        if (playerReceived == player) return

        tradeController.playersWaitingTrade[player.name] = playerReceived.name

        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "SENT_TRADE",
                player,
                emptyMap(),
                messagesConfig
            )
        )
        playerReceived.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "RECEIVED_TRADE",
                player,
                emptyMap(),
                messagesConfig
            )
        )

        val textComponentAccept = createJson(
            LanguageAPI.getTranslatedMessage("ACCEPT_TRADE_JSON", player, emptyMap(), messagesConfig),
            LanguageAPI.getTranslatedMessage("ACCEPT_TRADE_JSON_HOVER", player, emptyMap(), messagesConfig),
            "/tradeaccept ${player.name}"
        )
        val textComponentRecuse = createJson(
            LanguageAPI.getTranslatedMessage("DENY_TRADE_JSON", player, emptyMap(), messagesConfig),
            LanguageAPI.getTranslatedMessage("DENY_TRADE_JSON_HOVER", player, emptyMap(), messagesConfig),
            "/tradedeny ${player.name}"
        )

        playerReceived.spigot().sendMessage(textComponentAccept, textComponentRecuse)

        object : BukkitRunnable() {
            override fun run() {
                tradeController.playersWaitingTrade.remove(player.name)
            }
        }.runTaskLater(MillMCEconomy.instance, (20 * 20).toLong())
    }

    private fun createJson(text: String, textHover: String, command: String): TextComponent {
        val textComponentAccept = TextComponent(text)
        textComponentAccept.hoverEvent =
            HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder(textHover).create()
            )
        textComponentAccept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
        return textComponentAccept
    }
}