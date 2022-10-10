package br.com.thiaago.millmc.economy.trade.listeners

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.trade.data.controller.TradeController
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class TradeListeners(private val tradeController: TradeController) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        tradeController.tradesRequests.remove(event.player)
        val trade =
            tradeController.trades.firstOrNull { trade -> trade.playerSender == event.player || trade.playerSender == event.player }
                ?: return

        if (!tradeController.trades.contains(trade)) return

        val abstractView = MillMCEconomy.instance!!.viewFrame!![event.player]!!
        val context = abstractView.contexts.first { it.player == event.player } ?: return

        val itemsSender = tradeController.getItems(context, tradeController.getSlots(context, 'S'))
        val itemsTarget = tradeController.getItems(context, tradeController.getSlots(context, 'T'))

        tradeController.giveItems(itemsSender, trade.playerSender)
        tradeController.giveItems(itemsTarget, trade.playerTarget)
        tradeController.trades.remove(trade)

        tradeController.playSound(trade.playerSender)
        tradeController.playSound(trade.playerTarget)
    }


}