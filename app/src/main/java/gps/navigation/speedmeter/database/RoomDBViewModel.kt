package gps.navigation.speedmeter.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RoomDBViewModel( val dbHelper: DatabaseHelper) : ViewModel() {

    init {
        fetchCourses()
    }

    private fun fetchCourses() {
        viewModelScope.launch {
            try {
                val CoursesFromDb = dbHelper.getHistoryWithRoutePoints()
                // here you have your CoursesFromDb
            } catch (e: Exception) {
                // handler error
            }
        }
    }
}