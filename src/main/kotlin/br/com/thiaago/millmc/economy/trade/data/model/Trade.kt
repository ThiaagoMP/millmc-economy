package br.com.thiaago.millmc.economy.trade.data.model

import br.com.thiaago.millmc.economy.trade.data.model.phase.ITradePhase
import org.bukkit.entity.Player

data class Trade(
    val playerSender: Player,
    val playerTarget: Player,
    val timeStarted: Long,
    val firstPhase: ITradePhase,
    val secondPhase: ITradePhase
)