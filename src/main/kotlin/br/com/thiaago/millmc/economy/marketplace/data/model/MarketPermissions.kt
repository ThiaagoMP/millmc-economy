package br.com.thiaago.millmc.economy.marketplace.data.model

data class MarketPermissions(val permission: String, val quantity: Int) : Comparable<MarketPermissions> {

    override fun compareTo(other: MarketPermissions): Int {
        return if (quantity == other.quantity) 0 else if (quantity < other.quantity) 1 else -1
    }

}