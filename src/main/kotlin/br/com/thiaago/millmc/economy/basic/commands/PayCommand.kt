package br.com.thiaago.millmc.economy.basic.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.system.controller.AccountController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class PayCommand(private val accountController: AccountController, private val messagesConfig: FileConfiguration) {

    @Command(name = "pay", aliases = ["pagar"], target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        if (verifyArgs(context, player)) return

        val playerTarget = Bukkit.getOfflinePlayer(context.getArg(0))

        val amount = getAmount(context, player)
        if (amount == -9999) return

        val balancePlayer = accountController.getBalance(player.name)
        val balanceTarget = accountController.getBalance(playerTarget.name)

        if (verifyPlayerExists(playerTarget, player)) return
        if (verifyMoneyEnough(balancePlayer, player, amount)) return

        accountController.updateBalance(player.name, balancePlayer - amount)
        accountController.updateBalance(playerTarget.name, balanceTarget + amount)

        sendMessages(player, playerTarget, amount)
        playSounds(player, playerTarget)
    }

    private fun getAmount(
        context: Context<CommandSender>,
        player: Player
    ): Int {
        return try {
            context.getArg(1).toInt()
        } catch (exception: Exception) {
            LanguageAPI.getTranslatedMessage(
                "USE_NUMBER_TO_ARGS",
                player,
                emptyMap(),
                messagesConfig
            )
            -9999
        }
    }

    private fun playSounds(player: Player, playerTarget: OfflinePlayer) {
        player.playSound(player.location, Sound.CAT_MEOW, 4f, 1f)
        if (playerTarget.isOnline) (playerTarget as Player).playSound(
            player.location,
            Sound.LEVEL_UP,
            4f,
            1f
        )
    }

    private fun sendMessages(player: Player, playerTarget: OfflinePlayer, amount: Int) {
        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "YOU_PAID",
                player,
                mapOf(Pair("%player%", playerTarget.name), Pair("%amount%", amount.toString())),
                messagesConfig
            )
        )
        if (playerTarget.isOnline) {
            (playerTarget as Player).sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "YOU_RECEIVED",
                    player,
                    mapOf(Pair("%player%", player.name), Pair("%amount%", amount.toString())),
                    messagesConfig
                )
            )
        }
    }

    private fun verifyMoneyEnough(balance: Long, player: Player, amount: Int): Boolean {
        if (balance < amount) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "NOT_ENOUGH_MONEY",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            )
            return true
        }
        return false
    }

    private fun verifyPlayerExists(
        playerTarget: OfflinePlayer,
        player: Player
    ): Boolean {
        if (!accountController.hasAccount(playerTarget.name)) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "PLAYER_NOT_FOUND",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            )
            return true
        }
        return false
    }

    private fun verifyArgs(
        context: Context<CommandSender>,
        player: Player
    ): Boolean {
        if (context.argsCount() != 2) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "COMMAND_INCORRECT",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            )
            return true
        }
        return false
    }

}