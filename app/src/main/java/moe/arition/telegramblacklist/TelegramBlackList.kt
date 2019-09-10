package moe.arition.telegramblacklist

import android.util.SparseIntArray
import androidx.core.util.contains
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.*
import de.robv.android.xposed.XposedBridge.*
import java.util.ArrayList


class TelegramBlackList : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        //val switch = getModuleSharedPreferences()?.getBoolean("global_switch", false)
        //XposedBridge.log("Telegram Blacklist: Status: ${switch}")
        if (lpparam?.packageName.equals("org.telegram.messenger")) {
            XposedBridge.log("Telegram Blacklist: Loaded App: ${lpparam?.packageName}")
            XposedHelpers.findAndHookMethod("org.telegram.ui.ChatActivity\$ChatActivityAdapter", lpparam?.classLoader, "updateRows",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        try {
                            val parentObject = XposedHelpers.getSurroundingThis(param?.thisObject)
                            val messageList = XposedHelpers.getObjectField(parentObject, "messages") as ArrayList<Any>
                            if (messageList.size > 0) {
                                val currentAccount = XposedHelpers.getIntField(messageList[0], "currentAccount")
                                val messagesControllerClazz =
                                    XposedHelpers.findClass("org.telegram.messenger.MessagesController", lpparam?.classLoader)
                                val getMsgControllerMethod = XposedHelpers.findMethodBestMatch(
                                    messagesControllerClazz, "getInstance", Int::class.java
                                )
                                val messagesController = getMsgControllerMethod.invoke(null, currentAccount)
                                val getBlockedUsersMethod =
                                    XposedHelpers.findMethodBestMatch(messagesControllerClazz, "getBlockedUsers", Boolean::class.java)
                                getBlockedUsersMethod.invoke(messagesController, true)
                                val blockedUsers = XposedHelpers.getObjectField(messagesController, "blockedUsers") as SparseIntArray

                                val badMsgList = messageList.filter {
                                    val userId = XposedHelpers.getIntField(XposedHelpers.getObjectField(it, "messageOwner"), "from_id")
                                    val replyMsgObject = XposedHelpers.getObjectField(it, "replyMessageObject")
                                    var replyUserId = -100 // some invalid id
                                    if (replyMsgObject != null) {
                                        replyUserId = XposedHelpers.getIntField(
                                            XposedHelpers.getObjectField(replyMsgObject, "messageOwner")
                                            , "from_id"
                                        )
                                    }
                                    blockedUsers.contains(userId) || blockedUsers.contains(replyUserId)
                                }

                                for (messageObj in badMsgList) {
                                    val str = XposedHelpers.getObjectField(
                                        XposedHelpers.getObjectField(messageObj, "messageOwner"),
                                        "message"
                                    ) as String
                                    XposedBridge.log("Telegram Blacklist: Remove: $str")
                                    messageList.remove(messageObj)
                                }
                            }
                        } catch (e: Exception) {
                            XposedBridge.log("Telegram Blacklist: Failed: ${e}\n${e.printStackTrace()}")
                        }
                    }
                })
        }
    }
}