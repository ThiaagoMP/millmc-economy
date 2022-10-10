package br.com.thiaago.millmc.economy.marketplace.spigot.inventories

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.constants.items.BackItem
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.market.MarketCategoriesInventoryConfig
import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.MarketCategoriesInventory.MarketInventoryFields.KEY_CATEGORY
import br.com.thiaago.millmc.utils.ItemBuilder
import me.saiintbrisson.minecraft.OpenViewContext
import me.saiintbrisson.minecraft.View
import me.saiintbrisson.minecraft.ViewContext

class MarketCategoriesInventory(private val marketController: MarketController) : View(MarketInventoryFields.size) {

    object MarketInventoryFields {
        const val KEY_CATEGORY = "KEY_CATEGORY"
        val config =
            MillMCEconomy.instance!!.configController!!.configs[MarketCategoriesInventoryConfig::class.java]!!.getConfig()!!
        val size = config.getInt("INVENTORY.LINES")
    }

    init {
        isCancelOnClick = true
    }

    override fun onOpen(context: OpenViewContext) {
        context.setInventoryTitle(
            LanguageAPI.getTranslatedMessage(
                "TITLE",
                context.player,
                emptyMap(),
                MarketInventoryFields.config.getConfigurationSection("INVENTORY.TITLE")!!
            )
        )
    }

    override fun onRender(context: ViewContext) {
        marketController.marketCategories.forEach { marketCategory ->
            context.slot(
                marketCategory.itemIcon.slot, ItemBuilder(marketCategory.itemIcon.itemStack).setDisplayName(
                    LanguageAPI.getTranslatedMessage(
                        "DISPLAY_NAME",
                        context.player,
                        emptyMap(),
                        MarketInventoryFields.config.getConfigurationSection("CATEGORIES.${marketCategory.name}.ICON")
                    )
                ).setLore(
                    LanguageAPI.getTranslatedMessages(
                        "LORE",
                        context.player,
                        emptyMap(),
                        MarketInventoryFields.config.getConfigurationSection("CATEGORIES.${marketCategory.name}.ICON")
                    ).toMutableList()
                ).toItemStack()
            ).onClick { context ->
                MillMCEconomy.instance!!.viewFrame!!.open(
                    MarketItemsInventory::class.java, context.player, mapOf(Pair(KEY_CATEGORY, marketCategory))
                )
            }
        }
    }

}