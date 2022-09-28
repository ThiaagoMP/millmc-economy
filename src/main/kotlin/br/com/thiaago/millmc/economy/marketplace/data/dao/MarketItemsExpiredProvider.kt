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
import java.time.LocalDate

object MarketItemsExpiredTable : Table("teconomy_marketitemsexpired") {
    val id = integer("id").autoIncrement()
    val item = text("item")
    val price = long("price")
    val playerName = varchar("playerName", 20)
    val marketCategory = varchar("category", 50)
    val date = date("date")

    override val primaryKey = PrimaryKey(id, name = "PK_MARKET_ITEMS_EXPIRED_ID")
}

class MarketItemsExpiredProvider(private val database: Database) {

    fun createTable() {
        transaction(database) {
            SchemaUtils.create(MarketItemsExpiredTable)
        }
    }

    fun getAllMarketItemsExpired(marketController: MarketController): MutableList<MarketItem> {
        val listReturned = emptyList<MarketItem>().toMutableList()
        transaction(database) {
            MarketItemsExpiredTable.selectAll().forEach {
                val dateAnnounced: LocalDate = it[MarketItemsExpiredTable.date]
                listReturned.add(
                    MarketItem(
                        it[MarketItemsExpiredTable.id],
                        ItemBuilder.deserialize(JsonParser().parse(it[MarketItemsExpiredTable.item].toString()) as JsonObject)!!,
                        it[MarketItemsExpiredTable.price],
                        Bukkit.getOfflinePlayer(it[MarketItemsExpiredTable.playerName]),
                        marketController.getCategoryByName(it[MarketItemsExpiredTable.marketCategory]),
                        dateAnnounced
                    )
                )
            }
        }
        return listReturned
    }

    fun save(marketItem: MarketItem) {
        transaction(database) {
            MarketItemsExpiredTable.insert {
                it[id] = marketItem.id
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
            MarketItemsExpiredTable.deleteWhere { MarketItemsExpiredTable.id eq marketItem.id }
        }
    }

}