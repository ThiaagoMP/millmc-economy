package br.com.thiaago.millmc.economy.system.dao

import br.com.thiaago.millmc.economy.system.entities.Account
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object AccountsTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val uuid = varchar("uuid", 20)
    val balance = long("balance")
}

class AccountProvider(private val database: Database) {

    fun createTable() {
        transaction(database) {
            SchemaUtils.create(AccountsTable)
        }
    }

    fun getBalance(uuid: UUID): Long {
        var money = -9999L
        transaction(database) {
            try {
                money = AccountsTable.select { AccountsTable.uuid eq uuid.toString() }
                    .first { it[AccountsTable.uuid] == uuid.toString() }[AccountsTable.balance]
            } catch (_: Exception) {
            }
        }
        return money
    }

    fun deleteAccount(uuid: UUID) {
        transaction(database) {
            AccountsTable.deleteWhere { AccountsTable.uuid eq uuid.toString() }
        }
    }

    fun insert(account: Account) {
        transaction(database) {
            AccountsTable.insert {
                it[uuid] = account.uuid.toString()
                it[balance] = account.balance
            }
        }
    }

    fun update(account: Account) {
        transaction(database) {
            AccountsTable.update({ AccountsTable.uuid eq account.uuid.toString() }) {
                it[balance] = account.balance
            }
        }
    }

}