package br.com.thiaago.millmc.economy.marketplace.data.model

import br.com.thiaago.millmc.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import java.time.LocalDate

data class MarketItem(
    var id: Int,
    val itemStack: ItemStack,
    val price: Long,
    val playerOwner: OfflinePlayer,
    val marketCategory: MarketCategory,
    val dateAnnounced: LocalDate
) {

    class MarketItemBuilder {
        var id: Int = 0
        private var itemStack: ItemStack = ItemBuilder(Material.PAPER)
        private var price: Long = 100L
        private var playerOwner: OfflinePlayer? = null
        private var marketCategory: MarketCategory? = null
        private var dateAnnounced: LocalDate = LocalDate.now()

        fun setId(id: Int): MarketItemBuilder {
            this.id = id
            return this
        }

        fun setItemStack(itemStack: ItemStack): MarketItemBuilder {
            this.itemStack = itemStack
            return this
        }

        fun setPrice(price: Long): MarketItemBuilder {
            this.price = price
            return this
        }

        fun setPlayerOwner(playerOwner: OfflinePlayer): MarketItemBuilder {
            this.playerOwner = playerOwner
            return this
        }

        fun setMarketCategory(marketCategory: MarketCategory): MarketItemBuilder {
            this.marketCategory = marketCategory
            return this
        }

        fun setDateAnnounced(dateAnnounced: LocalDate): MarketItemBuilder {
            this.dateAnnounced = dateAnnounced
            return this
        }

        fun build(): MarketItem = MarketItem(id, itemStack, price, playerOwner!!, marketCategory!!, dateAnnounced)


    }

}

