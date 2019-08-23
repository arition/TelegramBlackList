package moe.arition.telegramblacklist

import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.*
import de.robv.android.xposed.XposedBridge.*
import java.util.ArrayList


class Tutorial : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        //val switch = getModuleSharedPreferences()?.getBoolean("global_switch", false)
        //XposedBridge.log("Telegram Blacklist: Status: ${switch}")
        if (lpparam?.packageName.equals("org.telegram.messenger")) {
            XposedBridge.log("Telegram Blacklist: Loaded App: ${lpparam?.packageName}")
            XposedHelpers.findAndHookMethod("org.telegram.ui.ChatActivity\$ChatActivityAdapter", lpparam?.classLoader,
                "updateRows", object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        try {
                            val parentObject = XposedHelpers.getSurroundingThis(param?.thisObject)
                            val messageList = XposedHelpers.getObjectField(parentObject, "messages") as ArrayList<Any>
                            val badMsgList = messageList.filter {
                                XposedHelpers.getIntField(
                                    XposedHelpers.getObjectField(it, "messageOwner"),
                                    "from_id"
                                ) == 309428750
                            }
                            for (messageObj in badMsgList) {
                                val str = XposedHelpers.getObjectField(
                                    XposedHelpers.getObjectField(messageObj, "messageOwner"),
                                    "message"
                                ) as String
                                XposedBridge.log("Telegram Blacklist: Remove: $str")
                                messageList.remove(messageObj)
                            }
                        } catch (e: Exception) {
                            XposedBridge.log("Telegram Blacklist: Failed: ${e}\n${e.printStackTrace()}")
                        }
                    }
                })
            hookAllConstructors(
                lpparam?.classLoader?.loadClass("org.telegram.messenger.MessageObject"),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        try {
                            val message = XposedHelpers.getObjectField(param?.thisObject, "messageOwner")
                            val senderId = readInstanceProperty<Int>(message, "from_id")
                            val msgString = readInstanceProperty<String>(message, "message")
                            //XposedBridge.log("Telegram Blacklist: Id: $senderId, Msg: $msgString")
                            if (senderId == 241396061) {
                                //writeInstanceProperty(message, "message", "blocked")

                            }
                        } catch (e: Exception) {
                            XposedBridge.log("Telegram Blacklist: Failed: ${e}\n${e.printStackTrace()}")
                        }
                    }
                })
        }
    }
}