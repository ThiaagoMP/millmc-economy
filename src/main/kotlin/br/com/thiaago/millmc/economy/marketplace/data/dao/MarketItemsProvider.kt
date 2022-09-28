package br.com.thiaago.millmc.economy.marketplace.data.dao

import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.utils.ItemBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction

object MarketItemsTable : Table("teconomy_marketitems") {
    val id = integer("id").autoIncrement()
    val item = text("item")
    val price = long("price")
    val playerName = varchar("playerName", 20)
    val marketCategory = varchar("category", 50)
    val date = date("date")

    override val primaryKey = PrimaryKey(id, name = "PK_MARKET_ITEMS_ID")
}

class MarketItemsProvider(private val database: Database) {

    fun createTable() {
        transaction(database) {
            SchemaUtils.create(MarketItemsTable)
        }
    }

    fun getAllMarketItems(marketController: MarketController): MutableList<MarketItem> {
        val listReturned = emptyList<MarketItem>().toMutableList()
        transaction(database) {
            MarketItemsTable.selectAll().forEach {
                listReturned.add(
                    MarketItem(
                        it[MarketItemsTable.id],
                        ItemBuilder.deserialize(JsonParser().parse(it[MarketItemsTable.item].toString()) as JsonObject)!!,
                        it[MarketItemsTable.price],
                        Bukkit.getOfflinePlayer(it[MarketItemsTable.playerName]),
                        marketController.getCategoryByName(it[MarketItemsTable.marketCategory]),
                        it[MarketItemsTable.date]
                    )
                )
            }
        }
        return listReturned
    }

    fun save(marketItem: MarketItem) {
        transaction(database) {
            MarketItemsTable.insert {
                it[item] = ItemBuilder.serialize(marketItem.itemStack).toString()
                it[price] = marketItem.price
                it[playerName] = marketItem.playerOwner.name
                it[marketCategory] = marketItem.marketCategory.name
                it[date] = marketItem.dateAnnounced
            }
        }
    }

    fun remove(marketItem: MarketItem) {
        transaction(database) {
            MarketItemsTable.deleteWhere { MarketItemsTable.id eq marketItem.id }
        }
    }

}