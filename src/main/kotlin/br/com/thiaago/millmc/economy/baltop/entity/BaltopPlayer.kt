package br.com.thiaago.millmc.economy.baltop.entity

import org.bukkit.OfflinePlayer

data class BaltopPlayer(val player: OfflinePlayer, val money: Long, val position: Short)