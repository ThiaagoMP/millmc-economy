package br.com.thiaago.millmc.economy.system.spigot.listener

import br.com.thiaago.millmc.economy.system.controller.AccountController
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListeners(private val accountController: AccountController) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        accountController.loadAccount(event.player.name)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        accountController.removeAccountInCache(event.player.name)
    }

}