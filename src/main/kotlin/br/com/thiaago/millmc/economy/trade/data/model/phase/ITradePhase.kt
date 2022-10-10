package br.com.thiaago.millmc.economy.trade.data.model.phase

interface ITradePhase {

    fun isAcceptPlayerSender(): Boolean
    fun isAcceptPlayerTarget(): Boolean

    fun setAcceptPlayerSender(accept: Boolean)
    fun setAcceptPlayerTarget(accept: Boolean)

}