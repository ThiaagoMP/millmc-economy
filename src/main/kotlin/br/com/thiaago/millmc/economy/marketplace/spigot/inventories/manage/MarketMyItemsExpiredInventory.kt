package br.com.thiaago.millmc.economy.marketplace.spigot.inventories.manage

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.api.inventory.PagedInventory
import br.com.thiaago.millmc.constants.items.BackItem
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.config.impl.market.MarketMyItemsExpiredInventoryConfig
import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.utils.ItemBuilder
import me.saiintbrisson.minecraft.OpenViewContext
import me.saiintbrisson.minecraft.PaginatedViewSlotContext
import me.saiintbrisson.minecraft.ViewContext
import me.saiintbrisson.minecraft.ViewItem

private object MarketInventoryFields {
    val config =
        MillMCEconomy.instance!!.configController.configs[MarketMyItemsExpiredInventoryConfig::class.java]!!.getConfig()!!
}

class MarketMyItemsExpiredInventory(private val marketController: MarketController) :
    PagedInventory<MarketItem>() {

    init {
        isCancelOnClick = true
    }

    override fun onOpen(context: OpenViewContext) {
        context.setInventoryTitle(
            LanguageAPI.getTranslatedMessage(
                "TITLE",
                context.player,
                emptyMap(),
                MarketInventoryFields.config.getConfigurationSection("INVENTORY.TITLE")
            )
        )
    }

    override fun onRender(context: ViewContext) {
        context.slot(45, BackItem.getItem(context.player)).onClick { context.open(MarketMyItemsInventory::class.java) }
        val items = marketController.marketItemsExpired.filter { context.player.uniqueId == it.playerOwner.uniqueId }

        context.paginated<MarketItem>().source = items
        context.set("ITEMS", items)
    }

    override fun onItemRender(context: PaginatedViewSlotContext<MarketItem>, viewItem: ViewItem, value: MarketItem) {
        viewItem.withItem(ItemBuilder(value.itemStack)).onClick {
            if (context.player.inventory.firstEmpty() == -1) {
                context.player.sendMessage(
                    LanguageAPI.getTranslatedMessage(
                        "INVENTORY_FULL",
                        context.player,
                        emptyMap(),
                        MillMCEconomy.instance!!.configController.configs[MessagesConfig::class.java]!!.getConfig()!!
                    )
                )
                return@onClick
            }
            marketController.marketItemsExpired.remove(value)
            context.close()
            context.player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "MARKET_ITEM_EXPIRED_REMOVED",
                    context.player,
                    emptyMap(),
                    MillMCEconomy.instance!!.configController.configs[MessagesConfig::class.java]!!.getConfig()!!
                )
            )
            context.player.inventory.addItem(value.itemStack)
        }
    }

}