package br.com.thiaago.millmc.economy.trade.inventory

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.TradeConfig
import br.com.thiaago.millmc.utils.ItemBuilder
import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object TradeInventoryFields {
    const val SLOT_ACCEPT_PLAYER_SENDER = 47
    const val SLOT_ACCEPT_PLAYER_TARGET = 51
    val SLOTS_PLAYER_SENDER = listOf(9, 10, 12, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39)
    val SLOTS_PLAYER_TARGET = listOf(14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44)
    val config = MillMCEconomy.instance!!.configController!!.configs[TradeConfig::class.java]!!.getConfig()!!
    val acceptItemSection: ConfigurationSection = config.getConfigurationSection("INVENTORY.ACCEPT_ITEM")
}

class TradeInventory(
    private val playerSender: Player,
    private val playerTarget: Player,
    tradeConfig: FileConfiguration
) {
    private val inventory: Inventory = Bukkit.createInventory(
        null,
        6 * 9,
        LanguageAPI.getTranslatedMessage(
            "TITLE",
            playerSender,
            emptyMap(),
            TradeInventoryFields.config.getConfigurationSection("INVENTORY")
        )
    )

    init {
        setBorder(tradeConfig)

        setItem(46, getSkullItem(playerSender))
        setItem(52, getSkullItem(playerTarget))

        val inkSender: ItemStack = getButtonAccept(playerSender, tradeConfig)
        val inkTarget: ItemStack = getButtonAccept(playerTarget, tradeConfig)

        setItem(47, inkSender)
        setItem(51, inkTarget)

        playerTarget.openInventory(inventory)
        playerSender.openInventory(inventory)
    }

    private fun getSkullItem(playerSender: Player) = ItemBuilder(
        Material.SKULL_ITEM,
        1,
        SkullType.PLAYER.ordinal.toShort()
    ).setDisplayName("§2" + playerSender.name).setOwner(playerSender.name)

    private fun getButtonAccept(
        playerSender: Player,
        tradeConfig: FileConfiguration
    ) = NBTEditor.set(
        ItemBuilder(
            Material.INK_SACK,
            1,
            8.toShort()
        ).setDisplayName(
            LanguageAPI.getTranslatedMessage(
                "DISPLAY_NAME",
                playerSender,
                emptyMap(),
                tradeConfig.getConfigurationSection("INVENTORY.ACCEPT_ITEM")
            )
        ).toItemStack(),
        playerSender.name,
        "name"
    )

    private fun setBorder(tradeConfig: FileConfiguration) {
        val borderItemSection = tradeConfig.getConfigurationSection("INVENTORY.BORDER_ITEM")

        val borderItem: ItemBuilder =
            ItemBuilder(Material.getMaterial(borderItemSection.getString("TYPE"))).setDurabilitys(
                borderItemSection.getInt("DATA").toShort()
            ).setDisplayName(
                LanguageAPI.getTranslatedMessage(
                    "DISPLAY_NAME",
                    playerSender,
                    emptyMap(),
                    borderItemSection
                )
            )

        for (i in 0..8) {
            setItem(i, borderItem)
        }
        for (i in 48..50) {
            if (i == 49) continue
            setItem(i, borderItem)
        }
        setItem(13, borderItem)
        setItem(22, borderItem)
        setItem(31, borderItem)
        setItem(40, borderItem)
    }

    private fun setItem(slot: Int, item: ItemStack) {
        inventory.setItem(slot, item)
    }
}