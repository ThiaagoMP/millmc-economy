package br.com.thiaago.millmc.economy.system.controller

import br.com.thiaago.millmc.economy.MillMCEconomy
import br.com.thiaago.millmc.economy.system.dao.AccountProvider
import br.com.thiaago.millmc.economy.system.entities.Account

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

    internal fun loadAccount(name: String): Account {
        val hasAccountInCache = getAccountInCache(name)
        if (hasAccountInCache != null) return hasAccountInCache

        val hasAccountInTable = containsAccountInTable(name)
        val account = Account(name, hasAccountInTable.second)

        if (!hasAccountInTable.first) account.balance = balanceInitial

        accounts.add(account)
        accountsToSave.add(account)
        return account
    }

    fun hasAccount(name: String): Boolean {
        val accountInCache = getAccountInCache(name)
        if (accountInCache != null) return true

        return accountProvider.hasAccount(name)
    }

    fun updateBalance(name: String, newBalance: Long): Account {
        val account = getAccountInCache(name) ?: Account(name, accountProvider.getBalance(name))
        account.balance = newBalance
        if (accountsToSave.removeIf { it.name == account.name }) accountsToSave.add(account)
        return account
    }

    fun removeAccountInCache(name: String) {
        accounts.remove(getAccountInCache(name))
    }

    fun getBalance(name: String): Long {
        val accountInCache = getAccountInCache(name)
        if (accountInCache != null) return accountInCache.balance
        return accountProvider.getBalance(name)
    }

    fun saveAccounts() {
        accountsToSave.forEach {
            val hasAccount = containsAccountInTable(it.name)
            if (hasAccount.first) accountProvider.update(it)
            else accountProvider.insert(it)
        }
        accountsToSave.clear()
    }

    private fun getAccountInCache(name: String): Account? {
        return try {
            accounts.first { account: Account -> account.name == name }
        } catch (exception: Exception) {
            null
        }
    }

    private fun containsAccountInTable(name: String): Pair<Boolean, Long> {
        val balance = accountProvider.getBalance(name)
        return Pair(balance != balanceIfNot, balance)
    }

}