package br.com.thiaago.millmc.economy

import br.com.thiaago.millmc.MillMCCore
import br.com.thiaago.millmc.economy.basic.BasicLoader
import br.com.thiaago.millmc.economy.basic.baltop.controller.BaltopController
import br.com.thiaago.millmc.economy.config.ConfigController
import br.com.thiaago.millmc.economy.config.impl.BaltopConfig
import br.com.thiaago.millmc.economy.config.impl.market.MarketCategoriesInventoryConfig
import br.com.thiaago.millmc.economy.loader.ViewFrameLoader
import br.com.thiaago.millmc.economy.marketplace.MarketLoader
import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import br.com.thiaago.millmc.economy.marketplace.data.dao.MarketItemsExpiredProvider
import br.com.thiaago.millmc.economy.marketplace.data.dao.MarketItemsProvider
import br.com.thiaago.millmc.economy.system.controller.AccountController
import br.com.thiaago.millmc.economy.system.dao.AccountProvider
import br.com.thiaago.millmc.economy.system.runnable.SaveAccountsRunnable
import br.com.thiaago.millmc.economy.system.spigot.listener.PlayerListeners
import me.saiintbrisson.bukkit.command.BukkitFrame
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
    var baltopController: BaltopController? = null

    private var accountProvider: AccountProvider? = null
    private var marketItemsProvider: MarketItemsProvider? = null
    private var marketItemsExpiredProvider: MarketItemsExpiredProvider? = null

    var marketController: MarketController? = null

    var viewFrame: ViewFrame? = null

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        if (initDatabase()) return
        accountController = AccountController(accountProvider = accountProvider!!)

        baltopController = BaltopController(
            baltopConfig = configController.configs[BaltopConfig::class.java]!!.getConfig()!!,
            accountProvider = accountProvider!!
        )

        initSaveAccountsTask()

        marketController = MarketController(
            categoriesConfig = configController.configs[MarketCategoriesInventoryConfig::class.java]!!.getConfig()!!,
            marketItemsProvider = marketItemsProvider!!,
            marketItemsExpiredProvider = marketItemsExpiredProvider!!
        )

        Bukkit.getPluginManager().registerEvents(PlayerListeners(accountController!!), this)
        viewFrame = ViewFrameLoader.load(this)

        val bukkitFrame = BukkitFrame(this)
        BasicLoader.load(this, bukkitFrame, accountController!!)
        MarketLoader.load(bukkitFrame)

        //just for testing, reload
        Bukkit.getOnlinePlayers().forEach { accountController!!.loadAccount(it.name) }
    }

    private fun initDatabase(): Boolean {
        accountProvider = AccountProvider(MillMCCore.instance!!.database!!)
        marketItemsExpiredProvider = MarketItemsExpiredProvider(MillMCCore.instance!!.database!!)
        marketItemsProvider = MarketItemsProvider(MillMCCore.instance!!.database!!)
        accountProvider?.createTable() ?: return true
        marketItemsProvider?.createTable() ?: return true
        marketItemsExpiredProvider?.createTable() ?: return true
        return false
    }

    private fun initSaveAccountsTask() {
        val timeToSaveAccounts = config.getLong("TIME_TO_SAVE_ACCOUNTS") * 20 * 60
        SaveAccountsRunnable(accountController!!).runTaskTimerAsynchronously(
            this, timeToSaveAccounts, timeToSaveAccounts
        )
    }

    override fun onDisable() {
        accountController!!.saveAccounts()
    }

}