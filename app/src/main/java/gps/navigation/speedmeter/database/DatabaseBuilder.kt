package gps.navigation.speedmeter.database

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {
    private var INSTANCE: HistoryDatabase? = null

    fun getInstance(context: Context): HistoryDatabase {
        if (INSTANCE == null) {
            synchronized(HistoryDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            HistoryDatabase::class.java,
            "history_database"
        ).fallbackToDestructiveMigration().build()
}