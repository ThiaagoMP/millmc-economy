package br.com.thiaago.millmc.economy.marketplace.data.negotiation.impl

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.economy.marketplace.data.negotiation.Negotiation
import br.com.thiaago.millmc.economy.system.controller.AccountController
import me.saiintbrisson.minecraft.ViewSlotClickContext
import org.bukkit.configuration.file.FileConfiguration

class DenyNegotiation : Negotiation {

    override fun execute(
        context: ViewSlotClickContext,
        accountController: AccountController,
        marketController: MarketController,
        marketItem: MarketItem,
        messagesConfig: FileConfiguration
    ) {
        context.close()
        context.player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "MARKET_DENY_BUY", context.player, emptyMap(), messagesConfig
            )
        )
    }
}