package br.com.thiaago.millmc.economy

import br.com.thiaago.millmc.MillMCCore
import br.com.thiaago.millmc.economy.baltop.controller.BaltopController
import br.com.thiaago.millmc.economy.baltop.inventory.BaltopInventory
import br.com.thiaago.millmc.economy.basic.BasicLoader
import br.com.thiaago.millmc.economy.config.ConfigController
import br.com.thiaago.millmc.economy.config.impl.BaltopConfig
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.system.controller.AccountController
import br.com.thiaago.millmc.economy.system.dao.AccountProvider
import br.com.thiaago.millmc.economy.system.runnable.SaveAccountsRunnable
import br.com.thiaago.millmc.economy.system.spigot.listener.PlayerListeners
import me.saiintbrisson.minecraft.ViewFrame
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MillMCEconomy : JavaPlugin() {

    companion object {
        var instance: MillMCEconomy? = null
            private set
    }

    val configController = ConfigController().load(this)

    var accountController: AccountController? = null
    var accountProvider: AccountProvider? = null
    var baltopController: BaltopController? = null

    var viewFrame: ViewFrame? = null

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        accountProvider = AccountProvider(MillMCCore.instance!!.database)
        accountProvider?.createTable() ?: return
        accountController = AccountController(accountProvider = accountProvider!!)

        baltopController = BaltopController(
            baltopConfig = configController.configs[BaltopConfig::class.java]!!.getConfig()!!,
            accountProvider = accountProvider!!
        )

        val timeToSaveAccounts = config.getLong("TIME_TO_SAVE_ACCOUNTS") * 20 * 60

        SaveAccountsRunnable(accountController!!).runTaskTimerAsynchronously(
            this,
            timeToSaveAccounts,
            timeToSaveAccounts
        )

        Bukkit.getPluginManager().registerEvents(PlayerListeners(accountController!!), this)
        viewFrame = ViewFrame.of(
            this, BaltopInventory(
                baltopController!!,
                configController.configs[BaltopConfig::class.java]!!.getConfig()!!,
                configController.configs[MessagesConfig::class.java]!!.getConfig()!!
            )
        ).register()

        BasicLoader.load(this, accountController!!)

        //just for testing, reload
        Bukkit.getOnlinePlayers().forEach { accountController!!.loadAccount(it.name) }
    }

    override fun onDisable() {
        accountController!!.saveAccounts()
    }

}