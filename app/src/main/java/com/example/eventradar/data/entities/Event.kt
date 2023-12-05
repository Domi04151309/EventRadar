package com.example.eventradar.data.entities

import android.icu.text.SimpleDateFormat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "event")
data class Event(
    @ColumnInfo(name = "event_id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "organizer_id") val organizerId: Long,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "start") val start: Long,
    @ColumnInfo(name = "end") val end: Long,
    @ColumnInfo(name = "address_id") val addressId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image") val image: String,
) {
    fun getStartAsString(): String =
        SimpleDateFormat(
            "d. MMM yyyy 'um' H:mm 'Uhr'",
            Locale.getDefault(),
        ).format(start)

    fun getPriceAsString(): String = String.format("%.2f", price) + " €"

    fun getSummary(): String =
        "${
            SimpleDateFormat(
                "d. MMM yyyy",
                Locale.getDefault(),
            ).format(start)
        } • ${getPriceAsString()}"
}
