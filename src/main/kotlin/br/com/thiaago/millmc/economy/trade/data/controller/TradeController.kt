package br.com.thiaago.millmc.economy.trade.data.controller

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.trade.data.model.Trade
import br.com.thiaago.millmc.economy.trade.data.model.phase.impl.TradePhase
import br.com.thiaago.millmc.economy.trade.inventories.TradeInventory
import me.saiintbrisson.minecraft.ViewContext
import me.saiintbrisson.minecraft.ViewItem
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TradeController(
    val trades: MutableList<Trade> = emptyList<Trade>().toMutableList(),
    val tradesRequests: MutableMap<Player, Player> = emptyMap<Player, Player>().toMutableMap()
) {

    fun startTrade(playerSender: Player, playerReceiver: Player) {
        val trade = Trade(playerSender, playerReceiver, System.currentTimeMillis(), TradePhase(), TradePhase())
        trades.add(trade)
        MillMCEconomy.instance!!.viewFrame!!.open(
            TradeInventory::class.java,
            playerSender,
            mapOf(Pair("TRADE", trade))
        )
        MillMCEconomy.instance!!.viewFrame!!.open(
            TradeInventory::class.java,
            playerReceiver,
            mapOf(Pair("TRADE", trade))
        )
    }

    fun endTrade(context: ViewContext, itemsSender: List<ViewItem>, itemsTarget: List<ViewItem>, trade: Trade) {
        giveItems(itemsSender, trade.playerTarget)
        giveItems(itemsTarget, trade.playerSender)
        trades.remove(trade)

        playSound(trade.playerSender)
        playSound(trade.playerTarget)
        context.close()
    }

    fun playSound(player: Player) {
        player.playSound(player.location, Sound.LEVEL_UP, 4f, 1f)
    }

    fun giveItems(
        itemsSender: List<ViewItem>,
        player: Player
    ) {
        itemsSender.forEach { item ->
            if (player.inventory.firstEmpty() == -1)
                player.world.dropItem(player.location, item.item as ItemStack)
            else
                player.inventory.addItem(item.item as ItemStack)
        }
    }

    fun getSlots(context: ViewContext, char: Char): List<Int> =
        context.layoutPatterns.first { pattern -> pattern.character == char }.slots.toList()

    fun getItems(context: ViewContext, slots: List<Int>): List<ViewItem> {
        val listReturn = emptyList<ViewItem>().toMutableList()
        slots.forEach { listReturn.add(context.getItem(it)) }
        return listReturn
    }

}