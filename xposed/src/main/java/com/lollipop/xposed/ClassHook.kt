package com.lollipop.xposed

import de.robv.android.xposed.XposedHelpers

open class ClassHook(
    private val classLoader: ClassLoader,
    private val clazzName: String
) {

    private val clazz: Class<*> by lazy {
        XposedHelpers.findClass(clazzName, classLoader)
    }

    fun allMethods(name: String, callbackBinder: HookCallbackBinder) {
        clazz.hookAllMethods(name, callbackBinder)
    }

    fun method(name: String, param: Array<Class<*>>, callbackBinder: HookCallbackBinder) {
        clazz.hookMethod(
            name,
            param,
            callbackBinder
        )
    }

    fun method(name: String, param: Array<String>, callbackBinder: HookCallbackBinder) {
        val paramClass = param.map { XposedHelpers.findClass(it, classLoader) }.toTypedArray()
        method(name, paramClass, callbackBinder)
    }

    fun allConstructor(callbackBinder: HookCallbackBinder) {
        clazz.hookAllConstructors(callbackBinder)
    }

    fun constructor(param: Array<Class<*>>, callbackBinder: HookCallbackBinder) {
        clazz.hookConstructor(param, callbackBinder)
    }

    fun constructor(param: Array<String>, callbackBinder: HookCallbackBinder) {
        val paramClass = param.map { XposedHelpers.findClass(it, classLoader) }.toTypedArray()
        constructor(paramClass, callbackBinder)
    }

    fun method(
        methodName: String,
        callbackBinder: HookCallbackBinder
    ) {
        method(
            methodName,
            arrayOf<Class<*>>(),
            callbackBinder
        )
    }

    fun constructor(callbackBinder: HookCallbackBinder) {
        constructor(arrayOf<Class<*>>(), callbackBinder)
    }

}

fun ClassLoader.hookClass(clazz: String, callback: ClassHook.() -> Unit) {
    ClassHook(this, clazz).apply(callback)
}