package br.com.thiaago.millmc.economy.trade.controller

import br.com.thiaago.millmc.economy.trade.model.Trade
import org.bukkit.entity.Player

class TradeController(
    val playersWaitingTrade: MutableMap<String, String> = emptyMap<String, String>().toMutableMap(),
    val trades: MutableList<Trade> = emptyList<Trade>().toMutableList()
) {

    fun getTrade(player: Player): Trade? {
        return trades.firstOrNull { trade: Trade -> trade.playerSender == player || trade.playerTarget == player }
    }

    fun getOtherPlayer(trade: Trade, player: Player): Player =
        if (trade.playerTarget == player) trade.playerSender else trade.playerTarget

}