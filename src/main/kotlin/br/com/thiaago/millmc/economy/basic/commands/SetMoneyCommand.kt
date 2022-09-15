package br.com.thiaago.millmc.economy.basic.commands

import br.com.thiaago.millmc.economy.system.controller.AccountController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class SetMoneyCommand(private val accountController: AccountController) {

    @Command(name = "setmoney", permission = "setmoney.use", target = CommandTarget.ALL)
    fun handleCommand(context: Context<CommandSender>) {
        if (context.argsCount() != 2) {
            context.sender.sendMessage("§cPlease use /setmoney (nick) (amount)")
            return
        }
        val playerTarget = Bukkit.getOfflinePlayer(context.getArg(0))
        val amount = getAmount(context)

        if (amount == -9999L || amount < 0) {
            context.sendMessage("§cPlease use only whole and positive numbers.")
            return
        }

        if (!accountController.hasAccount(playerTarget.name)) {
            context.sendMessage("§cAccount not found on nick ${playerTarget.player.name}")
            return
        }

        accountController.updateBalance(playerTarget.name, amount)
        context.sendMessage("§aPlayer money ${playerTarget.player.name} has been changed to $amount.")
    }

    private fun getAmount(
        context: Context<CommandSender>
    ): Long {
        return try {
            context.getArg(1).toLong()
        } catch (exception: Exception) {
            return -9999
        }
    }

}