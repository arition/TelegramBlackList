package moe.arition.telegramblacklist

import androidx.preference.PreferenceDataStore
import org.json.JSONArray
import org.json.JSONObject

class JsonPreferenceDataStore: PreferenceDataStore() {
    companion object {
        var jsonData: String = "{}"
    }

    override fun getInt(key: String?, defValue: Int): Int {
        if (key == null)
            return defValue
        val data = JSONObject(jsonData)
        return try {
            data.getInt(key)
        } catch (e: Exception) {
            defValue
        }
    }

    override fun putInt(key: String?, value: Int) {
        if (key == null)
            return
        val data = JSONObject(jsonData)
        data.put(key, value)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        if (key == null)
            return defValue
        val data = JSONObject(jsonData)
        return try {
            data.getBoolean(key)
        } catch (e: Exception) {
            defValue
        }
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == null)
            return
        val data = JSONObject(jsonData)
        data.put(key, value)
    }

    override fun getString(key: String?, defValue: String?): String? {
        if (key == null)
            return defValue
        val data = JSONObject(jsonData)
        return try {
            data.getString(key)
        } catch (e: Exception) {
            defValue
        }
    }

    override fun putString(key: String?, value: String?) {
        if (key == null)
            return
        val data = JSONObject(jsonData)
        data.put(key, value)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        if (key == null)
            if (defValues == null)
                return HashSet()
            else
                return defValues
        val data = JSONObject(jsonData)
        return try {
            val set = HashSet<String>()
            val jsonArr = data.getJSONArray(key)
            for (i in 0..jsonArr.length()) {
                set.add(jsonArr.getString(i))
            }
            set
        } catch (e: Exception) {
            if (defValues == null)
                HashSet()
            else
                defValues
        }
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?) {
        if (key == null)
            return
        val data = JSONObject(jsonData)
        val jarray = JSONArray(values)
        data.put(key, jarray)
    }
}