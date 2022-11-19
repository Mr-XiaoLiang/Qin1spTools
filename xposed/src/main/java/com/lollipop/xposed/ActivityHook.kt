package com.lollipop.xposed

import android.os.Bundle

class ActivityHook(
    classLoader: ClassLoader,
    clazzName: String
): ClassHook(classLoader, clazzName) {

    fun onCreate(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onCreate",
            arrayOf(Bundle::class.java),
            callbackBinder
        )
    }

    fun onStart(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onStart",
            callbackBinder
        )
    }

    fun onAttachedToWindow(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onAttachedToWindow",
            callbackBinder
        )
    }

    fun onRestart(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onRestart",
            callbackBinder
        )
    }

    fun onResume(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onResume",
            callbackBinder
        )
    }

    fun onPause(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onPause",
            callbackBinder
        )
    }

    fun onStop(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onStop",
            callbackBinder
        )
    }

    fun onDestroy(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onDestroy",
            callbackBinder
        )
    }

    fun onDetachedFromWindow(
        callbackBinder: HookCallbackBinder
    ) {
        method(
            "onDetachedFromWindow",
            callbackBinder
        )
    }

}

fun ClassLoader.hookActivity(clazz: String, callback: ActivityHook.() -> Unit) {
    ActivityHook(this, clazz).apply(callback)
}