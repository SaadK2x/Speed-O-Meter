package gps.navigation.speedmeter.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface HistoryDao {

    @Query("SELECT * FROM history_table")
    fun getHistoryWithRoutePoints(): List<HistoryWithRoutePoints>

    @Query("SELECT * FROM history_table where pointsID=:id")
    fun getSpecificHistoryAndRoute(id: Int): HistoryWithRoutePoints

    @Query("DELETE FROM history_table")
    fun getDeleteAll(): Int

    @Query("DELETE FROM history_table WHERE pointsID = :id")
    fun getDeleteSpecific(id: Int): Int

    @Insert
    fun insertHistory(history: History): Long

    @Insert
    fun insertRoutePoints(routePoints: List<RoutePoints>): List<Long>
}