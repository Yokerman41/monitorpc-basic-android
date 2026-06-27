package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComputerDao {
    @Query("SELECT * FROM computers WHERE ownerUsername = :ownerUsername ORDER BY id DESC")
    fun getComputersForUserFlow(ownerUsername: String): Flow<List<ComputerEntity>>

    @Query("SELECT * FROM computers WHERE id = :id LIMIT 1")
    suspend fun getComputerById(id: Int): ComputerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComputer(computer: ComputerEntity): Long

    @Update
    suspend fun updateComputer(computer: ComputerEntity)

    @Delete
    suspend fun deleteComputer(computer: ComputerEntity)
}
