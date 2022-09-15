package br.com.thiaago.millmc.economy.baltop.command

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.baltop.inventory.BaltopInventory
import me.saiintbrisson.minecraft.command.annotation.Command
import me.saiintbrisson.minecraft.command.command.Context
import me.saiintbrisson.minecraft.command.target.CommandTarget
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BaltopCommand(private val plugin: MillMCEconomy) {

    @Command(name = "baltop", target = CommandTarget.PLAYER)
    fun handleCommand(context: Context<CommandSender>) {
        plugin.viewFrame!!.open(BaltopInventory::class.java, context.sender as Player)
    }

}