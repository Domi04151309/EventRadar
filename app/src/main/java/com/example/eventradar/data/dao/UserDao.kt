package com.example.eventradar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.eventradar.data.entities.User
import com.example.eventradar.data.entities.UserWithAccount

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf User-Daten in der Datenbank.
 */
@Dao
interface UserDao {
    /**
     * Sucht nach einem User anhand der ID und gibt diesen zurück, falls vorhanden.
     */
    @Query("SELECT * FROM user WHERE account_id = :id LIMIT 1")
    suspend fun get(id: Long): UserWithAccount?

    /**
     * Fügt einen oder mehrere User in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg user: User)
}
