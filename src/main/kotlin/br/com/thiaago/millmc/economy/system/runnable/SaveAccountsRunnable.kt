package br.com.thiaago.millmc.economy.system.runnable

import br.com.thiaago.millmc.economy.system.controller.AccountController
import org.bukkit.scheduler.BukkitRunnable

class SaveAccountsRunnable(private val accountController: AccountController) : BukkitRunnable() {
    override fun run() {
        accountController.saveAccounts()
    }
}