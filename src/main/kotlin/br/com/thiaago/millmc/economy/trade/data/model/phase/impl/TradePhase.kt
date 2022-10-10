package br.com.thiaago.millmc.economy.trade.data.model.phase.impl

import br.com.thiaago.millmc.economy.trade.data.model.phase.ITradePhase

class TradePhase : ITradePhase {

    private var acceptPlayerSender: Boolean = false
    private var acceptPlayerTarget: Boolean = false

    override fun isAcceptPlayerSender(): Boolean {
        return acceptPlayerSender
    }

    override fun isAcceptPlayerTarget(): Boolean {
        return acceptPlayerTarget
    }

    override fun setAcceptPlayerSender(accept: Boolean) {
        acceptPlayerSender = accept
    }

    override fun setAcceptPlayerTarget(accept: Boolean) {
        acceptPlayerTarget = accept
    }
}