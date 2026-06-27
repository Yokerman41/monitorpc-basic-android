package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE username = :username LIMIT 1")
    fun getSettingsFlow(username: String): Flow<SettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE username = :username LIMIT 1")
    suspend fun getSettingsDirect(username: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsEntity)
}
