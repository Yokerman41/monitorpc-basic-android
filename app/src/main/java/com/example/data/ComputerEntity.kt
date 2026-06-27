package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "computers")
data class ComputerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ipAddress: String,
    val port: String,
    val username: String = "admin",
    val password: String = "••••••••",
    val ownerUsername: String = "", // Usuario que registró esta PC
    val apiKey: String = "" // Nueva columna para la seguridad real
)
