package gps.navigation.speedmeter.sharedprefrences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.Point


class SharedPreferenceHelperClass(private var context: Context) {


    private val MY_PREFERENCS = "preference"
    var sp: SharedPreferences = context.getSharedPreferences(MY_PREFERENCS, Context.MODE_PRIVATE)


    fun putString(key: String, value: String): Boolean {
        val editor = sp.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun putInt(key: String, value: Int): Boolean {
        val editor = sp.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun putLong(key: String, value: Long): Boolean {
        val editor = sp.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun getLong(key: String): Long {
        return sp.getLong(key, 0)
    }

    fun putBoolean(key: String, value: Boolean): Boolean {
        val editor = sp.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    fun putDouble(key: String, value: Float): Boolean {
        val editor = sp.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }


    fun getString(key: String, default: String): String {
        return sp.getString(key, default)!!
    }

    fun getInt(key: String): Int {
        return sp.getInt(key, 0)
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return sp.getBoolean(key, default)
    }


    fun putFloat(key: String, value: Float): Boolean {
        val editor = sp.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    fun getFloat(key: String): Float {
        return sp.getFloat(key, 0F)
    }

    fun putPoints(key: String, value: ArrayList<Point>): Boolean {
        val jsonString = Gson().toJson(value)
        return putString(key, jsonString)
    }

    fun putConstraints(key: String, value: ArrayList<Int>): Boolean {
        val jsonString = Gson().toJson(value)
        return putString(key, jsonString)
    }

    fun getPoints(key: String): ArrayList<Point> {
        val jsonString = getString(key, "")
        return if (jsonString.isNullOrEmpty()) {
            ArrayList()
        } else {
            Gson().fromJson(jsonString, object : TypeToken<ArrayList<Point>>() {}.type)
        }
    }

    fun getConstraints(key: String): ArrayList<Int> {
        val jsonString = getString(key, "")
        return if (jsonString.isNullOrEmpty()) {
            ArrayList()
        } else {
            Gson().fromJson(jsonString, object : TypeToken<ArrayList<Int>>() {}.type)
        }
    }

}