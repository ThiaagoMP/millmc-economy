package br.com.thiaago.millmc.economy.marketplace.data.negotiation.impl

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.economy.marketplace.data.negotiation.Negotiation
import br.com.thiaago.millmc.economy.system.controller.AccountController
import me.saiintbrisson.minecraft.ViewSlotClickContext
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration

class AcceptNegotiation : Negotiation {

    override fun execute(
        context: ViewSlotClickContext,
        accountController: AccountController,
        marketController: MarketController,
        marketItem: MarketItem,
        messagesConfig: FileConfiguration
    ) {
        val player = context.player
        val balancePlayer: Long = accountController.getBalance(player.name)

        if (player.inventory.firstEmpty() == -1) {
            context.close()
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "INVENTORY_FULL",
                    player,
                    emptyMap(),
                    messagesConfig
                )
            )
            player.playSound(player.location, Sound.CAT_MEOW, 4F, 1F)
            return
        }

        if (marketItem.price > balancePlayer) {
            context.close()
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "MARKET_NOT_MONEY",
                    player,
                    mapOf(Pair("%money%", (marketItem.price - balancePlayer).toString())),
                    messagesConfig
                )
            )
            player.playSound(player.location, Sound.CAT_MEOW, 4F, 1F)
            return
        }

        accountController.updateBalance(player.name, balancePlayer - marketItem.price)
        accountController.updateBalance(
            marketItem.playerOwner.name,
            accountController.getBalance(marketItem.playerOwner.name) + marketItem.price
        )

        context.close()
        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "MARKET_ITEM_PURCHASED",
                player,
                mapOf(Pair("%money%", (marketItem.price).toString())),
                messagesConfig
            )
        )
        player.playSound(player.location, Sound.LEVEL_UP, 4F, 1F)

        marketItem.playerOwner.player?.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "MARKET_YOUR_PURCHASED_ITEM",
                marketItem.playerOwner,
                mapOf(Pair("%money%", (marketItem.price).toString())),
                messagesConfig
            )
        ) ?: return

        marketItem.playerOwner.player?.playSound(marketItem.playerOwner.player.location, Sound.LEVEL_UP, 4F, 1F)
            ?: return
        marketController.removeItem(marketItem, false)
        player.inventory.addItem(marketItem.itemStack)
    }

}