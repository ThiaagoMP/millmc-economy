package br.com.thiaago.millmc.economy.trade.model

import org.bukkit.entity.Player

data class Trade(
    val playerSender: Player,
    val playerTarget: Player,
    var playerSenderAccept: Boolean = false,
    var playerTargetAccept: Boolean = false
)