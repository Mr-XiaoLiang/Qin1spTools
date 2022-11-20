package com.lollipop.xposed.impl

import android.app.Activity
import com.lollipop.xposed.tools.hookClass
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HotKeyHookImpl(private val pkg: String, private val loader: ClassLoader) {

    companion object {
        fun bind(param: XC_LoadPackage.LoadPackageParam): HotKeyHookImpl {
            val hookImpl = HotKeyHookImpl(param.packageName, param.classLoader)
            hookImpl.hookTo()
            return hookImpl
        }
    }

    private fun hookTo() {
        loader.hookClass("android.app.Activity") {
            method("onKeyDown", arrayOf(Int::class.java, android.view.KeyEvent::class.java)) {
                onBefore {
                    val target = it.thisObject
                    if (target is Activity) {
                        val args = it.args
                        if (args != null && args.size > 1) {
                            val code = args[0]
                            if (code is Int) {
                                HotKeyBroadcastReceiver.send(target, code, pkg)
                            }
                        }
                    }
                }
            }
        }
    }

}