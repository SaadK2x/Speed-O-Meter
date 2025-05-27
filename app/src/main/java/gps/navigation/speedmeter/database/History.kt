package gps.navigation.speedmeter.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "history_table")
data class History(
    @PrimaryKey val pointsID: Int,
    @ColumnInfo val source: String,
    @ColumnInfo val destination: String,
    @ColumnInfo val distance: Float,
    @ColumnInfo val speed: Float,
    @ColumnInfo val avgSpeed: Float,
    @ColumnInfo val maxSpeed: Float,
    @ColumnInfo val time: String,
    @ColumnInfo val date: String)
