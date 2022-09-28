package br.com.thiaago.millmc.economy.marketplace.spigot.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.config.impl.MessagesConfig
import br.com.thiaago.millmc.economy.marketplace.data.controller.MarketController
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MarketAnnounceCommand(private val marketController: MarketController) {

    private val messagesConfig =
        MillMCEconomy.instance!!.configController.configs[MessagesConfig::class.java]!!.getConfig()!!

    @Command(name = "market.announce", aliases = ["mercado.anunciar"], target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        if (verifications(player, context)) return

        val price = try {
            context.getArg(0).toLong()
        } catch (exception: Exception) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "COMMAND_INCORRECT_MARKET_ANNOUNCE", player, emptyMap(), messagesConfig
                )
            )
            return
        }

        marketController.addItem(player.inventory.itemInHand, price, player)
        player.inventory.remove(player.inventory.itemInHand)
        player.sendMessage(
            LanguageAPI.getTranslatedMessage(
                "MARKET_ITEM_ADDED", player, emptyMap(), messagesConfig
            )
        )
        player.playSound(player.location, Sound.LEVEL_UP, 4F, 1F)
    }

    private fun verifications(
        player: Player, context: Context<CommandSender>
    ): Boolean {
        if (player.inventory.itemInHand == null || player.inventory.itemInHand.type == Material.AIR) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "MARKET_ANNOUNCE_NOT_ITEM_IN_HAND", player, emptyMap(), messagesConfig
                )
            )
            return true
        }
        if (context.argsCount() != 1) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "COMMAND_INCORRECT_MARKET_ANNOUNCE", player, emptyMap(), messagesConfig
                )
            )
            return true
        }
        if (verifyPermission(player)) return true

        return false
    }

    private fun verifyPermission(player: Player): Boolean {
        val itemsInMarketQuantity = marketController.getItemsInMarket(player).size
        val itemsMax =
            marketController.marketPermissions.firstOrNull { player.hasPermission(it.permission) }?.quantity ?: 1

        if (itemsInMarketQuantity >= itemsMax) {
            player.playSound(player.location, Sound.CAT_MEOW, 4f, 1f)
            player.sendMessage(LanguageAPI.getTranslatedMessage("MARKET_ITEMS_MAX", player, emptyMap(), messagesConfig))
            return true
        }
        return false
    }


}