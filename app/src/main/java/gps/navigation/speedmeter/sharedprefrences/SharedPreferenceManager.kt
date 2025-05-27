package gps.navigation.andriodspeedometer.sharedPref

import android.content.Context

class SharedPreferenceManager(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    fun saveBackgroundState(viewId: Int, backgroundResource: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("background_$viewId", backgroundResource)
        editor.apply()
    }

    fun getBackgroundState(viewId: Int): Int {
        return sharedPreferences.getInt("background_$viewId", 0) // 0 is the default value
    }
}