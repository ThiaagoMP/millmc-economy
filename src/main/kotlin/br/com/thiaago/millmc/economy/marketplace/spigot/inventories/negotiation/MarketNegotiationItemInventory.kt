package br.com.thiaago.millmc.economy.marketplace.spigot.inventories.negotiation

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.config.impl.market.MarketNegotiationInventoryConfig
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.economy.marketplace.data.negotiation.impl.AcceptNegotiation
import br.com.thiaago.millmc.economy.marketplace.data.negotiation.impl.DenyNegotiation
import br.com.thiaago.millmc.economy.marketplace.spigot.inventories.MarketItemsInventory
import br.com.thiaago.millmc.economy.system.controller.AccountController
import br.com.thiaago.millmc.utils.ItemBuilder
import me.saiintbrisson.minecraft.OpenViewContext
import me.saiintbrisson.minecraft.View
import me.saiintbrisson.minecraft.ViewContext
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class MarketNegotiationItemInventory(private val accountController: AccountController) : View(
    MillMCEconomy.instance!!.configController!!.configs[MarketNegotiationInventoryConfig::class.java]!!.getConfig()
        ?.getConfigurationSection("INVENTORY")!!.getInt("LINES") * 9
) {

    private val sectionInventory =
        MillMCEconomy.instance!!.configController!!.configs[MarketNegotiationInventoryConfig::class.java]!!.getConfig()
            ?.getConfigurationSection("INVENTORY")
    private val sectionItems = sectionInventory!!.getConfigurationSection("ITEMS")

    private val acceptSection = sectionItems.getConfigurationSection("ACCEPT_ITEM")
    private val denySection = sectionItems.getConfigurationSection("DENY_ITEM")

    private val messagesConfig =
        MillMCEconomy.instance!!.configController!!.configs[MessagesConfig::class.java]!!.getConfig()!!

    private val marketController = MillMCEconomy.instance!!.marketController!!

    init {
        isCancelOnClick = true
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
        val acceptItem = getItem(acceptSection, context.player)
        val denyItem = getItem(denySection, context.player)

        val marketItem = context.get<MarketItem>(MarketItemsInventory.MarketItemsInventoryFields.KEY_ITEM)

        context.slot(acceptSection.getInt("SLOT"), acceptItem).onClick {
            AcceptNegotiation().execute(it, accountController, marketController, marketItem, messagesConfig)
        }

        context.slot(denySection.getInt("SLOT"), denyItem).onClick {
            DenyNegotiation().execute(it, accountController, marketController, marketItem, messagesConfig)
        }
    }

    private fun getItem(section: ConfigurationSection, player: Player) =
        ItemBuilder(Material.getMaterial(section.getString("TYPE"))).setDisplayName(
            LanguageAPI.getTranslatedMessage(
                "DISPLAY_NAME", player, emptyMap(), section
            )
        ).setLore(
            LanguageAPI.getTranslatedMessages(
                "LORE", player, emptyMap(), section
            ).toMutableList()
        ).setDurabilitys(section.getInt("DATA").toShort())

}