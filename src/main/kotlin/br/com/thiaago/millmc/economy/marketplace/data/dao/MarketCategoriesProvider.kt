package br.com.thiaago.millmc.economy.marketplace.data.dao

import br.com.thiaago.millmc.economy.marketplace.data.model.ItemIcon
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketCategory
import br.com.thiaago.millmc.economy.marketplace.data.model.MarketItem
import br.com.thiaago.millmc.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

class MarketCategoriesProvider {

    fun load(config: FileConfiguration): List<MarketCategory> {
        val listReturned = emptyList<MarketCategory>().toMutableList()

        val sectionCategories = config.getConfigurationSection("CATEGORIES")

        sectionCategories.getKeys(false).forEach {

            val sectionItemIcon = sectionCategories.getConfigurationSection("$it.ICON")

            val materials = emptyList<Material>().toMutableList()
            sectionCategories.getConfigurationSection(it).getStringList("IDS").forEach { material ->
                    if (material == "*") return@forEach
                    materials.add(Material.getMaterial(material))
                }

            listReturned.add(
                MarketCategory(
                    it, emptyList<MarketItem>().toMutableList(), ItemIcon(
                        ItemBuilder(Material.getMaterial(sectionItemIcon.getString("TYPE"))).setDurabilitys(
                            sectionItemIcon.getInt("DATA").toShort()
                        ), sectionItemIcon.getInt("SLOT")
                    ), materials
                )
            )
        }
        return listReturned
    }

}