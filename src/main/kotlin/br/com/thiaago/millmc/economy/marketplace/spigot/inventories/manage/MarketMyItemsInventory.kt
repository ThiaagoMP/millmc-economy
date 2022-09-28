package br.com.thiaago.millmc.economy.marketplace.spigot.inventories.manage

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.api.inventory.PagedInventory
import br.com.thiaago.millmc.constants.items.BackItem
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.config.impl.market.MarketMyItemsInventoryConfig
import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.MarketCategoriesInventory
import br.com.thiaago.millmc.utils.ItemBuilder
import me.saiintbrisson.minecraft.OpenViewContext
import me.saiintbrisson.minecraft.PaginatedViewSlotContext
import me.saiintbrisson.minecraft.ViewContext
import me.saiintbrisson.minecraft.ViewItem
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class MarketMyItemsInventory(private val marketController: MarketController) : PagedInventory<MarketItem>() {

    private var sectionInventory: ConfigurationSection? = null

    init {
        isCancelOnClick = true
        sectionInventory =
            MillMCEconomy.instance!!.configController.configs[MarketMyItemsInventoryConfig::class.java]!!.getConfig()
                ?.getConfigurationSection("INVENTORY")!!
    }

    override fun onOpen(context: OpenViewContext) {
        context.setInventoryTitle(
            LanguageAPI.getTranslatedMessage(
                "TITLE",
                context.player,
                emptyMap(),
                sectionInventory!!.getConfigurationSection("TITLE")
            )
        )
    }

    override fun onRender(context: ViewContext) {
        context.slot(45, BackItem.getItem(context.player))
            .onClick { context.open(MarketCategoriesInventory::class.java) }
        val items = getItems(context)
        context.paginated<MarketItem>().source = items
        context.set("ITEMS", items)

        val section = sectionInventory!!.getConfigurationSection("MY_ITEMS_EXPIRED")!!

        context.slot(section.getInt("SLOT"), getItem(section, context)).onClick {
            MillMCEconomy.instance!!.viewFrame!!.open(MarketMyItemsExpiredInventory::class.java, it.player)
        }
    }

    private fun getItems(context: ViewContext): List<MarketItem> {
        val listReturn = listOf<MarketItem>().toMutableList()
        marketController.marketCategories.forEach {
            listReturn.addAll(it.items.filter { marketItem -> marketItem.playerOwner.uniqueId == context.player.uniqueId })
        }
        return listReturn
    }

    override fun onItemRender(context: PaginatedViewSlotContext<MarketItem>, viewItem: ViewItem, value: MarketItem) {
        viewItem.withItem(
            ItemBuilder(value.itemStack).addLore(
                LanguageAPI.getTranslatedMessages(
                    "LORE_ADDED",
                    context.player,
                    emptyMap(),
                    sectionInventory!!.getConfigurationSection("ITEMS_PLAYER")
                )
            )
        ).onClick {
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
            value.marketCategory.items.remove(value)
            context.close()
            context.player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "MARKET_ITEM_REMOVED",
                    context.player,
                    emptyMap(),
                    MillMCEconomy.instance!!.configController.configs[MessagesConfig::class.java]!!.getConfig()!!
                )
            )
            context.player.inventory.addItem(value.itemStack)
        }
    }

    private fun getItem(
        section: ConfigurationSection,
        context: ViewContext
    ) = ItemBuilder(Material.getMaterial(section.getString("ICON.TYPE")))
        .setDisplayName(
            LanguageAPI.getTranslatedMessage(
                "DISPLAY_NAME",
                context.player,
                emptyMap(),
                section.getConfigurationSection("ICON")
            )
        ).setLore(
            LanguageAPI.getTranslatedMessages(
                "LORE",
                context.player,
                emptyMap(),
                section.getConfigurationSection("ICON")
            ).toMutableList()
        ).toItemStack()

}