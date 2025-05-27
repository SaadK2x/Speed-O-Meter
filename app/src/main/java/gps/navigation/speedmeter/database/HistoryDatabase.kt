package gps.navigation.speedmeter.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoutePoints::class, History::class], version = 3)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun HistoryDao(): HistoryDao
}