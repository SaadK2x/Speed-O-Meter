package gps.navigation.andriodspeedometer.sharedPref

import android.content.Context
import android.content.SharedPreferences

class SaveData(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    // Key for saving and loading the selected colors
    private val SELECTED_COLOR_KEY = "selected_color"

    fun saveSelectedColor(selectedColor: String) {
        val editor = sharedPreferences.edit()
        editor.putString(SELECTED_COLOR_KEY, selectedColor)
        editor.apply()
    }

    fun loadSelectedColor(): String {
        return sharedPreferences.getString(SELECTED_COLOR_KEY, "Red") ?: "Red"
    }
}