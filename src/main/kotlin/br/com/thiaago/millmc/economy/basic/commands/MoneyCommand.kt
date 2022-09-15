package br.com.thiaago.millmc.economy.basic.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.system.controller.AccountController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class MoneyCommand(private val accountController: AccountController, private val messagesConfig: FileConfiguration) {

    @Command(name = "money", aliases = ["balance", "bal", "moedas", "dinheiro"], target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        val replaces = emptyMap<String, String>().toMutableMap()

        val commandAction = context.argsCount() == 0

        val balance: Long = if (commandAction) accountController.getBalance(player.name)
        else accountController.getBalance(Bukkit.getOfflinePlayer(context.getArg(0)).name)

        if ((!commandAction) && verifyPlayerTargetExists(balance, player)) return

        replaces["%money%"] = balance.toString()
        replaces["%player%"] = if (commandAction) player.name else context.getArg(0)

        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "BASIC.MONEY_MESSAGE", player, replaces, messagesConfig
            )
        )
    }

    private fun verifyPlayerTargetExists(balance: Long, player: Player): Boolean {
        if (balance == -9999L) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "BASIC.PLAYER_NOT_FOUND", player, emptyMap(), messagesConfig
                )
            )
            return true
        }
        return false
    }

}