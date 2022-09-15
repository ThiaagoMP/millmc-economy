package br.com.thiaago.millmc.economy.baltop.runnable

import br.com.thiaago.millmc.economy.baltop.controller.BaltopController
import br.com.thiaago.millmc.economy.system.dao.AccountProvider
import org.bukkit.scheduler.BukkitRunnable

class BaltopRunnable(
    private val baltopController: BaltopController,
    private val limit: Int,
    private val accountProvider: AccountProvider
) :
    BukkitRunnable() {
    override fun run() {
        baltopController.players = accountProvider.getBaltop(limit)
    }
}