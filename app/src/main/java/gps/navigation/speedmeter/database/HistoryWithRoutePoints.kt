package gps.navigation.speedmeter.database

import androidx.room.Embedded
import androidx.room.Relation

data class HistoryWithRoutePoints(
    @Embedded val history: History,
    @Relation(
        parentColumn = "pointsID",
        entityColumn = "pointsID"
    )
    val routePoints: List<RoutePoints>
)
