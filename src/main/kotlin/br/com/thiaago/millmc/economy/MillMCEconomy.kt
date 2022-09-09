package br.com.thiaago.millmc.economy

import br.com.thiaago.millmc.MillMCCore
import br.com.thiaago.millmc.economy.system.controller.AccountController
import br.com.thiaago.millmc.economy.system.dao.AccountProvider
import org.bukkit.plugin.java.JavaPlugin

class MillMCEconomy : JavaPlugin() {

    companion object {
        var instance: MillMCEconomy? = null
            private set
    }

    var accountController: AccountController? = null
    val accountProvider = AccountProvider(MillMCCore.instance!!.database)

    override fun onEnable() {
        instance = this
        accountProvider.createTable()
        accountController = AccountController(accountProvider = accountProvider)
        saveDefaultConfig()
    }

}