package gps.navigation.speedmeter.sharedprefrences
import android.content.Context
import android.content.SharedPreferences


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


    fun getString(key: String,default:String): String {
        return sp.getString(key, default)!!
    }

    fun getInt(key: String): Int {
        return sp.getInt(key, 0)
    }

    fun getBoolean(key: String,default: Boolean): Boolean {
        return sp.getBoolean(key, default)
    }


    fun putFloat(key: String, value: Float): Boolean {
        val editor = sp.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    fun getFloat(key: String) : Float {
        return sp.getFloat(key,0F)
    }

}