package br.com.thiaago.millmc.economy.marketplace.data.model

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class ItemIcon(val itemStack: ItemStack, val slot: Int)

data class MarketCategory(
    val name: String,
    val items: MutableList<MarketItem>,
    val itemIcon: ItemIcon,
    val allowedIds: MutableList<Material>
)