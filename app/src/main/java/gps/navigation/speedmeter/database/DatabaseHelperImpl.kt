package gps.navigation.speedmeter.database

class DatabaseHelperImpl(val historyDatabase: HistoryDatabase) : DatabaseHelper {

    override suspend fun getHistoryWithRoutePoints()=historyDatabase.HistoryDao().getHistoryWithRoutePoints()

    override suspend fun getSpecificHistoryAndRoute(id:Int)=historyDatabase.HistoryDao().getSpecificHistoryAndRoute(id)

    override suspend fun insertHistory(history: History)=historyDatabase.HistoryDao().insertHistory(history)

    override suspend fun insertRoutePoints(routePoints: List<RoutePoints>)=historyDatabase.HistoryDao().insertRoutePoints(routePoints)

    override suspend fun getDeleteAll()=historyDatabase.HistoryDao().getDeleteAll()

    override suspend fun getDeleteSpecific(id:Int)=historyDatabase.HistoryDao().getDeleteSpecific(id)
}