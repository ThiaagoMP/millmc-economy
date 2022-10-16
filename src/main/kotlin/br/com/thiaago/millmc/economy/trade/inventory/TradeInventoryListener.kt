package br.com.thiaago.millmc.economy.trade.inventory

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.trade.controller.TradeController
import br.com.thiaago.millmc.economy.trade.model.Trade
import br.com.thiaago.millmc.utils.ItemBuilder
import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.DyeColor
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class TradeInventoryListener(
    private val tradeController: TradeController,
    private val messagesConfig: FileConfiguration,
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onClick(clickEvent: InventoryClickEvent) {
        if (clickEvent.clickedInventory == null) return
        if (clickEvent.clickedInventory.title == null) return

        val player: Player = clickEvent.whoClicked as Player
        val trade = tradeController.getTrade(player) ?: return

        if (clickEvent.click.isShiftClick) {
            clickEvent.isCancelled = true
            return
        }

        if (clickEvent.clickedInventory == clickEvent.whoClicked.inventory) return

        val playerIsSender = player == trade.playerSender

        verifySlotsReceiveItems(playerIsSender, clickEvent)
        if (verifyAcceptButtons(clickEvent, player, playerIsSender, trade)) return
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        val trade = tradeController.getTrade(player) ?: return
        val otherPlayer = tradeController.getOtherPlayer(trade, player)
        val playerIsSender = trade.playerSender == player

        tradeController.trades.remove(trade)
        otherPlayer.closeInventory()
        if (playerIsSender) giveBackItems(player, otherPlayer, event) else giveBackItems(otherPlayer, player, event)
    }

    private fun giveBackItems(
        playerSender: Player,
        playerTarget: Player,
        event: InventoryCloseEvent
    ) {
        TradeInventoryFields.SLOTS_PLAYER_SENDER.forEach {
            if (event.inventory.getItem(it) != null) playerSender.inventory.addItem(event.inventory.getItem(it))
        }
        TradeInventoryFields.SLOTS_PLAYER_TARGET.forEach {
            if (event.inventory.getItem(it) != null) playerTarget.inventory.addItem(event.inventory.getItem(it))
        }
        sendToPlayerMessageTradeDenied(playerSender)
        sendToPlayerMessageTradeDenied(playerTarget)
    }

    private fun sendToPlayerMessageTradeDenied(player: Player) {
        player.updateInventory()
        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "TRADE_DENIED", player, emptyMap(), messagesConfig
            )
        )
        player.playSound(player.location, Sound.CAT_MEOW, 4f, 1f)
    }

    private fun verifySlotsReceiveItems(isSender: Boolean, clickEvent: InventoryClickEvent) {
        if (isSender) {
            if (!TradeInventoryFields.SLOTS_PLAYER_SENDER.contains(clickEvent.slot)) clickEvent.isCancelled = true
        } else if (!TradeInventoryFields.SLOTS_PLAYER_TARGET.contains(clickEvent.slot)) clickEvent.isCancelled = true
    }

    private fun verifyAcceptButtons(
        clickEvent: InventoryClickEvent,
        player: Player,
        playerIsSender: Boolean,
        trade: Trade
    ): Boolean {
        if (clickEvent.slot == TradeInventoryFields.SLOT_ACCEPT_PLAYER_SENDER || clickEvent.slot == TradeInventoryFields.SLOT_ACCEPT_PLAYER_TARGET) {
            val playerNbt: String = NBTEditor.getString(clickEvent.currentItem, "name")

            if (player.name != playerNbt) {
                clickEvent.isCancelled = true
                return true
            }

            val acceptItemSection = TradeInventoryFields.acceptItemSection

            if (playerIsSender) {
                configureItemAccept(clickEvent, acceptItemSection, trade.playerSenderAccept)
                trade.playerSenderAccept = !trade.playerSenderAccept
            } else {
                configureItemAccept(clickEvent, acceptItemSection, trade.playerTargetAccept)
                trade.playerTargetAccept = !trade.playerTargetAccept
            }

            if (trade.playerSenderAccept && trade.playerTargetAccept)
                executeTrade(trade, clickEvent.inventory)

        }
        return false
    }

    private fun configureItemAccept(
        clickEvent: InventoryClickEvent,
        acceptItemSection: ConfigurationSection,
        accepted: Boolean
    ) {
        clickEvent.currentItem = if (accepted)
            ItemBuilder(clickEvent.currentItem).setColor(DyeColor.valueOf(acceptItemSection.getString("COLOR_INITIAL")))
        else ItemBuilder(clickEvent.currentItem).setColor(DyeColor.valueOf(acceptItemSection.getString("COLOR_ACCEPT")))
    }

    private fun executeTrade(trade: Trade, inventory: Inventory) {
        for (i in TradeInventoryFields.SLOTS_PLAYER_TARGET) {
            if (inventory.getItem(i) != null) trade.playerSender.inventory.addItem(inventory.getItem(i))
        }
        for (i in TradeInventoryFields.SLOTS_PLAYER_SENDER) {
            if (inventory.getItem(i) != null) trade.playerTarget.inventory.addItem(inventory.getItem(i))
        }
        tradeController.trades.remove(trade)

        sendToPlayerMessageTradePerformed(trade.playerSender)
        sendToPlayerMessageTradePerformed(trade.playerTarget)
    }

    private fun sendToPlayerMessageTradePerformed(player: Player) {
        player.closeInventory()
        player.playSound(player.location, Sound.LEVEL_UP, 4f, 1f)
        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "TRADE_PERFORMED",
                player,
                emptyMap(),
                messagesConfig
            )
        )
    }

}