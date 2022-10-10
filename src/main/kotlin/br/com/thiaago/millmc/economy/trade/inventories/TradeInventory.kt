package br.com.thiaago.millmc.economy.trade.inventories

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.trade.data.controller.TradeController
import br.com.thiaago.millmc.economy.trade.data.model.Trade
import br.com.thiaago.millmc.utils.ItemBuilder
import me.saiintbrisson.minecraft.*
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TradeInventory(private val config: FileConfiguration, private val tradeController: TradeController) : View() {

    init {
        isCancelOnShiftClick = true
        isCancelOnClone = true
        isCancelOnDrop = true
    }

    override fun onClick(context: ViewSlotClickContext) {
        if (context.isShiftClick || context.isMiddleClick) context.isCancelled = true
    }

    override fun onRender(context: ViewContext) {
        context.setLayout(
            "BBBBBBBBB", "BSSSBTTTB", "BSSSBTTTB", "BSSSBTTTB", "BSSSBTTTB", "BBBBBBBBB"
        )
        context.setLayout('S') { item: ViewItem ->
            item.withItem(ItemBuilder(Material.AIR))
        }
        context.setLayout('T') { item: ViewItem ->
            item.withItem(ItemBuilder(Material.AIR))
        }
        val trade = context.get<Trade>("TRADE")

        val sectionInventory = config.getConfigurationSection("INVENTORY")
        val sectionBorderItem = sectionInventory.getConfigurationSection("BORDER_ITEM")
        val sectionAcceptItem = sectionInventory.getConfigurationSection("ACCEPT_ITEM")

        configureBorderItem(sectionBorderItem, context)
        //configureSlotsReceiveItems(context, trade)

        configureButtonsAccept(sectionAcceptItem, context, sectionBorderItem, trade)
    }

    override fun onOpen(context: OpenViewContext) {
        context.setInventoryTitle(
            LanguageAPI.getTranslatedMessage(
                "TITLE", context.player, emptyMap(), config.getConfigurationSection("INVENTORY")
            )
        )
    }

    override fun onClose(context: ViewContext) {
        val trade = context.get<Trade>("TRADE")

        if (!tradeController.trades.contains(trade)) return

        val itemsSender = getItems(getSlots(context, 'S'), context)
        val itemsTarget = getItems(getSlots(context, 'T'), context)

        giveItems(itemsSender, trade.playerSender)
        giveItems(itemsTarget, trade.playerTarget)
        tradeController.trades.remove(trade)

        playSound(trade.playerSender)
        playSound(trade.playerTarget)
        if (context.player != trade.playerSender) trade.playerSender.closeInventory()
        if (context.player != trade.playerTarget) trade.playerTarget.closeInventory()
    }

    override fun onClick(context: ViewSlotContext) {
        verifySlot(context)
    }

    override fun onItemHold(context: ViewSlotContext) {
        verifySlot(context)
    }

    override fun onItemRelease(fromContext: ViewSlotContext, toContext: ViewSlotContext) {
        verifySlot(fromContext)
        verifySlot(toContext)
    }

    override fun onMoveOut(context: ViewSlotMoveContext) {
        verifySlot(context)
    }

    private fun verifySlot(context: ViewSlotContext) {
        val trade = context.get<Trade>("TRADE")

        val slotsSender = getSlots(context, 'S')
        val slotsTarget = getSlots(context, 'T')

        if (slotsSender.contains(context.slot) && context.player == trade.playerTarget) context.isCancelled = true
        if (slotsTarget.contains(context.slot) && context.player == trade.playerSender) context.isCancelled = true
    }

    private fun configureButtonsAccept(
        sectionAcceptItem: ConfigurationSection,
        context: ViewContext,
        sectionBorderItem: ConfigurationSection,
        trade: Trade
    ) {
        val itemAccept = ItemBuilder(Material.getMaterial(sectionAcceptItem.getString("TYPE"))).setDisplayName(
            LanguageAPI.getTranslatedMessage(
                "DISPLAY_NAME", context.player, emptyMap(), sectionBorderItem
            )
        )
        context.slot(47, itemAccept.clone()).onClick { slotClickContext ->
            slotClickContext.isCancelled = true
            if (trade.playerTarget == context.player) return@onClick

            slotClickContext.slot(slotClickContext.slot).withItem(
                ItemBuilder(itemAccept.clone()).setColor(
                    DyeColor.getByDyeData(
                        if (trade.firstPhase.isAcceptPlayerSender()) sectionAcceptItem.getInt("COLOR_INITIAL")
                            .toByte() else sectionAcceptItem.getInt("COLOR_ACCEPT").toByte()
                    )
                )
            )
            trade.firstPhase.setAcceptPlayerSender(!trade.firstPhase.isAcceptPlayerSender())
            verifyEndTrade(trade, context)
        }
        context.slot(51, itemAccept.clone()).onClick { slotClickContext ->
            slotClickContext.isCancelled = true
            if (trade.playerSender == context.player) return@onClick
            slotClickContext.slot(slotClickContext.slot).withItem(
                ItemBuilder(itemAccept.clone()).setColor(
                    DyeColor.getByDyeData(
                        if (trade.firstPhase.isAcceptPlayerTarget()) sectionAcceptItem.getInt("COLOR_INITIAL")
                            .toByte() else sectionAcceptItem.getInt("COLOR_ACCEPT").toByte()
                    )
                )
            )
            trade.firstPhase.setAcceptPlayerTarget(!trade.firstPhase.isAcceptPlayerTarget())
            verifyEndTrade(trade, context)
        }
    }

    /* private fun configureSlotsReceiveItems(
         context: ViewContext, trade: Trade
     ) {
         context.setLayout('S') { item: ViewItem ->
             item.withItem(ItemBuilder(Material.AIR)).onClick {
                 if (trade.playerTarget == context.player) it.isCancelled = true
             }.onItemHold { if (trade.playerTarget == context.player) it.isCancelled = true }
                 .onItemRelease { t, u ->
                     if (trade.playerTarget == context.player) {
                         t.isCancelled = true
                         u.isCancelled = true
                     }
                 }.onMoveOut {
                     if (trade.playerTarget == context.player) it.isCancelled = true
                 }
         }
         context.setLayout('T') { item: ViewItem ->
             item.withItem(ItemBuilder(Material.AIR)).onClick {
                 if (trade.playerSender == context.player) it.isCancelled = true
             }.onItemHold { if (trade.playerSender == context.player) it.isCancelled = true }
                 .onItemRelease { t, u ->
                     if (trade.playerSender == context.player) {
                         t.isCancelled = true
                         u.isCancelled = true
                     }
                 }.onMoveOut {
                     if (trade.playerSender == context.player) it.isCancelled = true
                 }
         }
     }*/

    private fun configureBorderItem(
        sectionBorderItem: ConfigurationSection, context: ViewContext
    ) {
        context.setLayout('B') { item: ViewItem ->
            item.withItem(
                ItemBuilder(Material.getMaterial(sectionBorderItem.getString("TYPE"))).setDisplayName(
                    LanguageAPI.getTranslatedMessage(
                        "DISPLAY_NAME", context.player, emptyMap(), sectionBorderItem
                    )
                ).setDurabilitys(sectionBorderItem.getInt("DATA").toShort())
            ).onClick { it.isCancelled = true }
        }
    }

    private fun verifyEndTrade(
        trade: Trade, context: ViewContext
    ) {
        if (trade.firstPhase.isAcceptPlayerSender() && trade.firstPhase.isAcceptPlayerTarget()) tradeController.endTrade(
            context, getItems(getSlots(context, 'S'), context), getItems(getSlots(context, 'T'), context), trade
        )
    }

    private fun getSlots(context: ViewContext, char: Char): List<Int> = tradeController.getSlots(context, char)

    private fun getItems(slots: List<Int>, context: ViewContext): List<ViewItem> =
        tradeController.getItems(context, slots)

    private fun giveItems(
        items: List<ViewItem>, player: Player
    ) {
        items.forEach { item ->
            if (player.inventory.firstEmpty() == -1) player.world.dropItem(player.location, item.item as ItemStack)
            else player.inventory.addItem(item.item as ItemStack)
        }
    }

    private fun playSound(player: Player) {
        player.playSound(player.location, Sound.CAT_MEOW, 4f, 1f)
    }
}
