package br.com.thiaago.millmc.economy.system.dao

import br.com.thiaago.millmc.economy.basic.baltop.entity.BaltopPlayer
import br.com.thiaago.millmc.economy.system.dao.AccountsTable.balance
import br.com.thiaago.millmc.economy.system.entities.Account
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object AccountsTable : Table("economy_accounts") {
    val name = varchar("name", 20)

    @OptIn(ExperimentalUnsignedTypes::class)
    val balance = long("balance")
}

class AccountProvider(private val database: Database) {

    fun createTable() {
        transaction(database) {
            SchemaUtils.create(AccountsTable)
        }
    }

    fun hasAccount(name: String): Boolean {
        var has = false
        transaction(database) {
            has = AccountsTable.select { AccountsTable.name eq name.toString() }.firstOrNull() != null
        }
        return has
    }

    fun getBalance(name: String): Long {
        var money = -9999L
        transaction(database) {
            try {
                money = AccountsTable.select { AccountsTable.name eq name }
                    .first { it[AccountsTable.name] == name }[balance]
            } catch (_: Exception) {
            }
        }
        return money
    }

    fun deleteAccount(name: String) {
        transaction(database) {
            AccountsTable.deleteWhere { AccountsTable.name eq name }
        }
    }

    fun insert(account: Account) {
        transaction(database) {
            AccountsTable.insert {
                it[name] = account.name
                it[balance] = account.balance
            }
        }
    }

    fun update(account: Account) {
        transaction(database) {
            AccountsTable.update({ AccountsTable.name eq account.name }) {
                it[balance] = account.balance
            }
        }
    }

    fun getBaltop(limit: Int): MutableMap<Short, BaltopPlayer> {
        val map = emptyMap<Short, BaltopPlayer>().toMutableMap()

        transaction(database) {
            var position = 1
            AccountsTable.selectAll().orderBy(balance, order = SortOrder.DESC).limit(limit).forEach {
                if (position >= 10) return@transaction
                map[position.toShort()] =
                    BaltopPlayer(Bukkit.getOfflinePlayer(it[AccountsTable.name]), it[balance], position.toShort())
                position++
            }
        }
        return map
    }

}