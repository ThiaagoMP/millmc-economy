package br.com.thiaago.millmc.economy.basic.commands

import br.com.thiaago.millmc.api.LanguageAPI
import br.com.thiaago.millmc.economy.basic.constants.Constants
import br.com.thiaago.millmc.economy.system.controller.AccountController
import br.com.thiaago.millmc.utils.ItemBuilder
import io.github.bananapuncher714.nbteditor.NBTEditor
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class BankCheckCommand(
    private val accountController: AccountController, private val messagesConfig: FileConfiguration
) {

    @Command(name = "bankcheck", aliases = ["cheque"], target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        val player = context.sender as Player

        if (verifyArgs(context, player)) return
        if (verifyInventory(player)) return

        val amount = getAmount(context, player)
        if (amount == -9999L) return

        val balance = accountController.getBalance(player.name)

        if (verifyEnoughMoney(balance, amount, player)) return

        accountController.updateBalance(player.name, balance - amount)

        val item = getItem(player, amount)
        player.inventory.addItem(NBTEditor.set(item, amount, Constants.BANK_CHECK_NBT.value))

        player.playSound(player.location, Sound.LEVEL_UP, 4f, 1f)
        player.sendMessage(LanguageAPI.getTranslatedMessage("BASIC.CHECK_ADDED", player, emptyMap(), messagesConfig))
    }

    private fun verifyEnoughMoney(
        balance: Long,
        amount: Long,
        player: Player
    ): Boolean {
        if (balance < amount) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "BASIC.NOT_ENOUGH_MONEY", player, emptyMap(), messagesConfig
                )
            )
            return true
        }
        return false
    }

    private fun verifyInventory(player: Player): Boolean {
        if (player.inventory.firstEmpty() == -1) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "BASIC.INVENTORY_FULL", player, emptyMap(), messagesConfig
                )
            )
            return true
        }
        return false
    }

    private fun verifyArgs(
        context: Context<CommandSender>,
        player: Player
    ): Boolean {
        if (context.argsCount() != 1) {
            player.sendMessage(
                LanguageAPI.getTranslatedMessage(
                    "BASIC.COMMAND_INCORRECT_BANK_CHECK", player, emptyMap(), messagesConfig
                )
            )
            return true
        }
        return false
    }

    private fun getItem(player: Player, amount: Long): ItemStack {
        val title =
            LanguageAPI.getTranslatedMessage("BASIC.BANK_CHECK.DISPLAY_NAME", player, emptyMap(), messagesConfig)
        val lore = LanguageAPI.getTranslatedMessages(
            "BASIC.BANK_CHECK.LORE",
            player,
            mapOf(Pair("%player%", player.name), Pair("%amount%", amount.toString())),
            messagesConfig
        )

        val itemStack = NBTEditor.set(
            ItemBuilder(Material.PAPER).setDisplayName(title).setLore(lore.toMutableList()).toItemStack(), amount,
            Constants.BANK_CHECK_NBT.value
        )
        return NBTEditor.set(itemStack, "not_stack", Random.nextInt(0, 999999).toString())
    }


    private fun getAmount(
        context: Context<CommandSender>, player: Player
    ): Long {
        return try {
            context.getArg(0).toLong()
        } catch (exception: Exception) {
            LanguageAPI.getTranslatedMessage(
                "BASIC.USE_NUMBER_TO_ARGS", player, emptyMap(), messagesConfig
            )
            -9999
        }
    }

}