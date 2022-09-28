package br.com.thiaago.millmc.economy.basic.baltop.controller

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.basic.baltop.entity.BaltopPlayer
import br.com.thiaago.millmc.economy.basic.baltop.runnable.BaltopRunnable
import br.com.thiaago.millmc.economy.system.dao.AccountProvider
import org.bukkit.configuration.file.FileConfiguration

data class BaltopController(
    var players: MutableMap<Short, BaltopPlayer> = HashMap(),
    private val baltopConfig: FileConfiguration,
    private val accountProvider: AccountProvider
) {

    private val timeReload = baltopConfig.getInt("TIME_RELOAD")

    init {
        BaltopRunnable(
            this,
            baltopConfig.getInt("PLAYERS"),
            accountProvider
        ).runTaskTimerAsynchronously(MillMCEconomy.instance, 0, (20 * timeReload * 60).toLong())
    }

}