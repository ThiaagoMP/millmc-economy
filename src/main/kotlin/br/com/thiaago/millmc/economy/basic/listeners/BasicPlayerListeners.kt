package br.com.thiaago.millmc.economy.basic.listeners

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.basic.constants.Constants
import br.com.thiaago.millmc.economy.system.controller.AccountController
import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class BasicPlayerListeners(
    private val accountController: AccountController,
    private val messagesConfig: FileConfiguration
) :
    Listener {

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        val player = event.player
        val itemInHand = player.inventory.itemInHand ?: return

        if (!player.isSneaking) return
        if (!(event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) return
        if (!NBTEditor.contains(itemInHand, Constants.BANK_CHECK_NBT.value)) return

        val amount = NBTEditor.getLong(itemInHand, Constants.BANK_CHECK_NBT.value)

        accountController.updateBalance(player.name, accountController.getBalance(player.name) + amount)

        player.inventory.remove(itemInHand)
        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "CHECK_UTILIZED",
                player,
                mapOf(Pair("%money%", amount.toString())),
                messagesConfig
            )
        )
    }

}