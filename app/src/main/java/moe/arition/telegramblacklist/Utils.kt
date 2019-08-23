package moe.arition.telegramblacklist

import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import java.lang.ref.WeakReference


@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
    return instance::class.java.getField(propertyName).get(instance) as R
}

fun <R> writeInstanceProperty(instance: Any, propertyName: String, value: R) {
    instance::class.java.getField(propertyName).set(instance, value)
}

var weakModulePreferences: WeakReference<XSharedPreferences?> = WeakReference(null)

fun getModuleSharedPreferences(): XSharedPreferences? {
    var preferences = weakModulePreferences.get()
    if (preferences == null) {
        preferences = XSharedPreferences(BuildConfig.APPLICATION_ID)
        preferences.makeWorldReadable()
        if (!preferences.getFile().canRead()) {
            XposedBridge.log("Telegram Blacklist: Making Pref file readable")
            preferences.getFile().setReadable(true, false)
            preferences.reload();
        }
        weakModulePreferences = WeakReference(preferences)
    } else {
        preferences.reload()
    }
    return preferences
}