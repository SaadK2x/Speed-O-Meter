package gps.navigation.speedmeter.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "route_points",
    foreignKeys = [ForeignKey(
        entity = History::class,
        parentColumns = ["pointsID"],
        childColumns = ["pointsID"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoutePoints(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "pointsID") val pointsID: Int,
    @ColumnInfo(name = "lat") val lat: Double?,
    @ColumnInfo(name = "lng") val lng: Double?
)
