package br.com.thiaago.millmc.economy.marketplace.spigot.inventories

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.api.inventory.PagedInventory
import br.com.thiaago.millmc.constants.items.BackItem
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.config.impl.market.MarketItemsInventoryConfig
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketCategory
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.manage.MarketMyItemsInventory
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.negotiation.MarketNegotiationItemInventory
import br.com.thiaago.millmc.utils.ItemBuilder
import me.saiintbrisson.minecraft.OpenViewContext
import me.saiintbrisson.minecraft.PaginatedViewSlotContext
import me.saiintbrisson.minecraft.ViewContext
import me.saiintbrisson.minecraft.ViewItem
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection

class MarketItemsInventory : PagedInventory<MarketItem>() {

    object MarketItemsInventoryFields {
        val config =
            MillMCEconomy.instance!!.configController!!.configs[MarketItemsInventoryConfig::class.java]!!.getConfig()!!
        val messagesConfig =
            MillMCEconomy.instance!!.configController!!.configs[MessagesConfig::class.java]!!.getConfig()!!
        val defaultConfig = MillMCEconomy.instance!!.config!!
        val marketController = MillMCEconomy.instance!!.marketController!!

        const val KEY_CATEGORY = MarketCategoriesInventory.MarketInventoryFields.KEY_CATEGORY
        const val KEY_ITEM = "KEY_ITEM"
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
                MarketItemsInventoryFields.config.getConfigurationSection("INVENTORY.TITLE")!!
            )
        )
    }

    override fun onItemRender(context: PaginatedViewSlotContext<MarketItem>, viewItem: ViewItem, value: MarketItem) {
        val replaces = getReplaces(value)

        val lore = LanguageAPI.getTranslatedMessages(
            "LORE_ADDED",
            context.player,
            replaces,
            MarketItemsInventoryFields.config.getConfigurationSection("INVENTORY.ITEMS_PLAYERS")
        )

        viewItem.withItem(ItemBuilder(value.itemStack.clone()).addLore(lore).toItemStack()).onClick {
            if (it.player.name == value.playerOwner.name) {
                it.close()
                it.player.playSound(it.player.location, Sound.CAT_MEOW, 4F, 1F)
                it.player.sendMessage(
                    LanguageAPI.getTranslatedMessage(
                        "MARKET_BUY_OWN_ITEM",
                        it.player,
                        emptyMap(),
                        MarketItemsInventoryFields.messagesConfig
                    )
                )
                return@onClick
            }
            MillMCEconomy.instance!!.viewFrame!!.open(
                MarketNegotiationItemInventory::class.java,
                it.player,
                mapOf(Pair(MarketItemsInventoryFields.KEY_ITEM, value))
            )
        }
    }

    override fun onRender(context: ViewContext) {
        context.slot(45, BackItem.getItem(context.player)).onClick { context.open(MarketCategoriesInventory::class.java) }
        val marketCategory: MarketCategory = context.get(MarketItemsInventoryFields.KEY_CATEGORY)

        context.paginated<MarketItem>().source = marketCategory.items

        val section =
            MarketItemsInventoryFields.config.getConfigurationSection("INVENTORY.DEFAULT_ITEMS.YOUR_ITEMS")

        context.slot(section.getInt("SLOT"), getItem(section, context)).onClick {
            MillMCEconomy.instance!!.viewFrame!!.open(MarketMyItemsInventory::class.java, it.player)
        }
    }

    private fun getReplaces(value: MarketItem): Map<String, String> {
        val dateAdded = value.dateAnnounced.plusDays(
            MarketItemsInventoryFields.defaultConfig.getInt("DAYS_TO_EXPIRE_ITEMS_IN_MARKET").toLong()
        )
        return mapOf(
            Pair("%seller%", value.playerOwner.player.name),
            Pair("%price%", value.price.toString()),
            Pair(
                "%date_format_eua%",
                "${value.dateAnnounced.year}-${formatDate(value.dateAnnounced.monthValue)}-${formatDate(value.dateAnnounced.dayOfMonth)}"
            ),
            Pair(
                "%date_expired_format_eua%",
                "${dateAdded.year}-${formatDate(dateAdded.monthValue)}-${formatDate(dateAdded.dayOfMonth)}"
            ),
            Pair(
                "%date_format_br%",
                "${formatDate(value.dateAnnounced.dayOfMonth)}-${formatDate(value.dateAnnounced.monthValue)}-${value.dateAnnounced.year}"
            ),
            Pair(
                "%date_expired_format_br%",
                "${formatDate(dateAdded.dayOfMonth)}-${formatDate(dateAdded.monthValue)}-${dateAdded.year}"
            )
        )
    }

    private fun formatDate(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }

    private fun getItem(
        section: ConfigurationSection,
        context: ViewContext,
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