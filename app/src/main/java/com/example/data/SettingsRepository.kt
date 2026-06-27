package com.example.data

import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val settingsDao: SettingsDao,
    private val computerDao: ComputerDao
) {
    fun getSettingsFlow(username: String): Flow<SettingsEntity?> = settingsDao.getSettingsFlow(username)
    fun getComputersFlow(username: String): Flow<List<ComputerEntity>> = computerDao.getComputersForUserFlow(username)

    suspend fun getSettings(username: String): SettingsEntity {
        return settingsDao.getSettingsDirect(username) ?: SettingsEntity(username = username)
    }

    suspend fun saveSettings(settings: SettingsEntity) {
        settingsDao.saveSettings(settings)
    }

    suspend fun getComputerById(id: Int): ComputerEntity? {
        return computerDao.getComputerById(id)
    }

    suspend fun insertComputer(computer: ComputerEntity): Long {
        return computerDao.insertComputer(computer)
    }

    suspend fun updateComputer(computer: ComputerEntity) {
        computerDao.updateComputer(computer)
    }

    suspend fun deleteComputer(computer: ComputerEntity) {
        computerDao.deleteComputer(computer)
    }
}
