package br.com.thiaago.millmc.economy.marketplace.data.controller

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.marketplace.data.dao.MarketCategoriesProvider
import br.com.thiaago.millmc.economy.marketplace.data.dao.MarketItemsExpiredProvider
import br.com.thiaago.millmc.economy.marketplace.data.dao.MarketItemsProvider
import br.com.thiaago.millmc.economy.marketplace.data.dao.MarketQuantitiesDao
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketCategory
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketPermissions
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MarketController(
    var marketCategories: List<MarketCategory> = emptyList(),
    var marketItemsExpired: MutableList<MarketItem> = emptyList<MarketItem>().toMutableList(),
    var marketPermissions: List<MarketPermissions> = MarketQuantitiesDao.load(),
    categoriesConfig: FileConfiguration,
    private val marketItemsProvider: MarketItemsProvider,
    private val marketItemsExpiredProvider: MarketItemsExpiredProvider,
) {

    init {
        marketCategories = MarketCategoriesProvider().load(categoriesConfig)
        loadOtherCategory()

        val marketItems = marketItemsProvider.getAllMarketItems(this)
        val marketItemsExpired = marketItemsExpiredProvider.getAllMarketItemsExpired(this)

        val config = MillMCEconomy.instance!!.config
        marketItems.forEach {
            if (it.dateAnnounced.plusDays(config.getLong("DAYS_TO_EXPIRE_ITEMS_IN_MARKET")).isBefore(getTimeActual())) {
                marketItemsProvider.remove(it)
                marketItems.remove(it)
                marketItemsExpired.add(it)
                marketItemsExpiredProvider.save(it)
            } else
                it.marketCategory.items.add(it)
        }

        marketItemsExpired.forEach {
            if (it.dateAnnounced.plusDays(config.getLong("DAYS_TO_DELETE_ITEMS_EXPIRED_IN_MARKET"))
                    .isBefore(getTimeActual())
            ) {
                marketItemsExpired.remove(it)
                marketItemsExpiredProvider.remove(it)
            }
        }

    }

    fun addItem(itemStack: ItemStack, price: Long, offlinePlayer: OfflinePlayer) {
        marketCategories.forEach {
            if (it.allowedIds.contains(itemStack.type)) {
                val marketItem =
                    MarketItem.MarketItemBuilder().setItemStack(itemStack).setMarketCategory(it).setPrice(price)
                        .setDateAnnounced(getTimeActual()).setPlayerOwner(offlinePlayer).build()
                marketItem.id = 0
                marketItemsProvider.save(marketItem)
                marketItem.marketCategory.items.add(marketItem)
            }
        }
    }

    fun removeItem(marketItem: MarketItem, isExpiredItem: Boolean) {
        if (isExpiredItem) {
            marketItemsExpired.remove(marketItem)
            marketItemsExpiredProvider.remove(marketItem)
        } else {
            marketItem.marketCategory.items.remove(marketItem)
            marketItemsProvider.remove(marketItem)
        }
    }

    private fun getTimeActual(): LocalDate = LocalDate.now(
        Clock.fixed(Instant.now(), ZoneId.of("America/Sao_Paulo"))
    )

    fun getItemsInMarket(player: Player): List<MarketItem> {
        val list = emptyList<MarketItem>().toMutableList()
        marketCategories.forEach {
            list.addAll(it.items.filter { items -> items.playerOwner.name == player.name })
        }
        return list
    }

    private fun loadOtherCategory() {
        val allowedIds = emptyList<Material>().toMutableList()
        marketCategories.forEach { allowedIds.addAll(it.allowedIds) }

        val categoryOther = marketCategories.first { it.name == "OTHER" }

        Material.values().forEach {
            if (!allowedIds.contains(it)) categoryOther.allowedIds.add(it)
        }
    }

    fun getCategoryByName(name: String): MarketCategory {
        return marketCategories.first { it.name == name }
    }

}