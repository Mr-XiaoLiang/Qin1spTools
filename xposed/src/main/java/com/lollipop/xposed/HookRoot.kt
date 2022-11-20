package com.lollipop.xposed

import com.lollipop.xposed.impl.HotKeyHookImpl
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookRoot : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        lpparam ?: return
        HotKeyHookImpl.bind(lpparam)
    }

}