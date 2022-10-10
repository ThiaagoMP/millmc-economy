package br.com.thiaago.millmc.economy.basic

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.basic.baltop.command.BaltopCommand
import br.com.thiaago.millmc.economy.basic.commands.BankCheckCommand
import br.com.thiaago.millmc.economy.basic.commands.MoneyCommand
import br.com.thiaago.millmc.economy.basic.commands.PayCommand
import br.com.thiaago.millmc.economy.basic.commands.SetMoneyCommand
import br.com.thiaago.millmc.economy.basic.listeners.BasicPlayerListeners
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.system.controller.AccountController
import me.saiintbrisson.bukkit.command.BukkitFrame
import org.bukkit.Bukkit

class BasicLoader {

    companion object {
        fun load(plugin: MillMCEconomy, bukkitFrame: BukkitFrame, accountController: AccountController) {
            val messagesConfig = plugin.configController!!.configs[MessagesConfig::class.java]!!.getConfig()!!

            Bukkit.getPluginManager().registerEvents(
                BasicPlayerListeners(
                    accountController, messagesConfig
                ), plugin
            )
            bukkitFrame.registerCommands(
                BankCheckCommand(accountController, messagesConfig),
                MoneyCommand(accountController, messagesConfig),
                PayCommand(accountController, messagesConfig),
                SetMoneyCommand(accountController),
                BaltopCommand(plugin)
            )
        }
    }

}