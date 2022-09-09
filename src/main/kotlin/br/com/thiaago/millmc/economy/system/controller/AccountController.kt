package br.com.thiaago.millmc.economy.system.controller

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.system.dao.AccountProvider
import br.com.thiaago.millmc.economy.system.entities.Account
import java.util.*

class AccountController(
    private val accounts: MutableList<Account> = emptyList<Account>().toMutableList(),
    private val accountsToSave: MutableList<Account> = emptyList<Account>().toMutableList(),
    private val accountProvider: AccountProvider
) {
    private val balanceInitial = MillMCEconomy.instance!!.config.getLong("BALANCE_INITIAL")
    private val balanceIfNot = -9999L

    init {
        accountProvider.createTable()
    }

    internal fun loadAccount(uuid: UUID): Account {
        val hasAccountInCache = getAccountInCache(uuid)
        if (hasAccountInCache != null) return hasAccountInCache

        val hasAccountInTable = containsAccountInTable(uuid)
        val account = Account(uuid, hasAccountInTable.second)

        if (!hasAccountInTable.first)
            account.balance = balanceInitial

        accounts.add(account)
        accountsToSave.add(account)
        return account
    }

    fun updateBalance(account: Account, newBalance: Long): Account {
        account.balance = newBalance
        if (accountsToSave.removeIf { it.uuid == account.uuid })
            accountsToSave.add(account)
        return account
    }

    fun removeAccountInCache(uuid: UUID) {
        accounts.remove(getAccountInCache(uuid))
    }

    fun getAccountInCache(uuid: UUID): Account? {
        return try {
            accounts.first { account: Account -> account.uuid == uuid }
        } catch (exception: Exception) {
            null
        }
    }

    fun saveAccounts() {
        accountsToSave.forEach {
            val hasAccount = containsAccountInTable(it.uuid)
            if (hasAccount.first) accountProvider.update(it)
            else accountProvider.insert(it)
        }
        accountsToSave.clear()
    }

    private fun containsAccountInTable(uuid: UUID): Pair<Boolean, Long> {
        val balance = accountProvider.getBalance(uuid)
        return Pair(balance != balanceIfNot, balance)
    }

}