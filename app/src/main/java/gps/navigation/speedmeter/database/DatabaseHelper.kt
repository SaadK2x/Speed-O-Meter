package gps.navigation.speedmeter.database

interface DatabaseHelper {
    suspend  fun getHistoryWithRoutePoints(): List<HistoryWithRoutePoints>
    suspend  fun getSpecificHistoryAndRoute(id:Int): HistoryWithRoutePoints
    suspend  fun insertHistory(history: History):Long
    suspend  fun insertRoutePoints(routePoints:List<RoutePoints>):List<Long>
    suspend  fun getDeleteAll():Int
    suspend  fun getDeleteSpecific(id:Int):Int
}