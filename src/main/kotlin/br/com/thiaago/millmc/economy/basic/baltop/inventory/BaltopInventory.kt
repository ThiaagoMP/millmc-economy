package br.com.thiaago.millmc.economy.basic.baltop.inventory

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.basic.baltop.controller.BaltopController
import br.com.thiaago.millmc.utils.ItemBuilder
import me.saiintbrisson.minecraft.OpenViewContext
import me.saiintbrisson.minecraft.View
import me.saiintbrisson.minecraft.ViewType
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.configuration.file.FileConfiguration

class BaltopInventory(
    private val baltopController: BaltopController,
    baltopConfig: FileConfiguration,
    private val messagesConfig: FileConfiguration
) :
    View(baltopConfig.getInt("INVENTORY.LINES"), "", ViewType.CHEST) {

    val itemsSection = baltopConfig.getConfigurationSection("INVENTORY.ITEMS")

    override fun onOpen(context: OpenViewContext) {
        context.setInventoryTitle(
            LanguageAPI.getTranslatedMessage(
                "BAL_TOP_INVENTORY_TITLE",
                context.player,
                emptyMap(),
                messagesConfig
            )
        )
        itemsSection.getKeys(false).forEach {
            val section = itemsSection.getConfigurationSection(it)
            val position = section.getInt("positionInTop").toShort()
            val slot = section.getInt("slot")

            if (!baltopController.players.containsKey(position)) return@forEach

            val playerTop = baltopController.players[position]!!

            slot(
                slot,
                ItemBuilder(
                    Material.SKULL_ITEM,
                    1,
                    SkullType.PLAYER.ordinal.toShort()
                ).setDisplayName(
                    LanguageAPI.getTranslatedMessage(
                        "BAL_TOP_DISPLAY_NAME",
                        context.player,
                        mapOf(Pair("%player%", playerTop.player.name)),
                        messagesConfig
                    )
                ).setLore(
                    LanguageAPI.getTranslatedMessages(
                        "BAL_TOP_DISPLAY_LORE",
                        context.player,
                        mapOf(Pair("%position%°", position.toString()), Pair("%money%", playerTop.money.toString())),
                        messagesConfig
                    ).toMutableList()
                ).setOwner(playerTop.player.name)
            ).onClick { event ->
                event.player.closeInventory()
                event.player.performCommand("/profile ${playerTop.player.name}")
            }
        }
    }

}