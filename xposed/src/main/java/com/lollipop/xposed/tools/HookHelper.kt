package com.lollipop.xposed.tools

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import android.view.View
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.StringBuilder
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.*

typealias HookCallbackBinder = DefaultHookCallback.() -> Unit

fun ClassLoader.find(value: String): Class<*> {
    return XposedHelpers.findClass(value, this)
}

fun Class<*>.hookMethod(
    methodName: String,
    parameterTypes: Array<Class<*>>,
    callback: HookCallbackBinder
) {
    XposedBridge.hookMethod(
        XposedHelpers.findMethodExact(
            this,
            methodName,
            *parameterTypes
        ),
        DefaultHookCallback().apply(callback)
    )
}

fun Class<*>.hookAllMethods(
    methodName: String,
    callback: HookCallbackBinder
) {
    XposedBridge.hookAllMethods(this, methodName, DefaultHookCallback().apply(callback))
}

fun ClassLoader.hookMethod(
    clazz: String,
    methodName: String,
    parameterTypes: Array<Class<*>>,
    callback: HookCallbackBinder
) {
    find(clazz).hookMethod(methodName, parameterTypes, callback)
}

fun ClassLoader.hookAllMethods(
    clazz: String,
    methodName: String,
    callback: HookCallbackBinder
) {
    find(clazz).hookAllMethods(methodName, callback)
}

fun Class<*>.hookConstructor(
    parameterTypes: Array<Class<*>>,
    callback: HookCallbackBinder
) {
    XposedBridge.hookMethod(
        XposedHelpers.findConstructorExact(this, *parameterTypes),
        DefaultHookCallback().apply(callback)
    )
}

fun Class<*>.hookAllConstructors(
    callback: HookCallbackBinder
) {
    XposedBridge.hookAllConstructors(this, DefaultHookCallback().apply(callback))
}

fun ClassLoader.hookConstructor(
    clazz: String,
    parameterTypes: Array<Class<*>>,
    callback: HookCallbackBinder
) {
    find(clazz).hookConstructor(parameterTypes, callback)
}

fun ClassLoader.hookAllConstructors(
    clazz: String,
    callback: HookCallbackBinder
) {
    find(clazz).hookAllConstructors(callback)
}

fun Any.logValue(): String {
    when (this) {
        is Array<*> -> {
            val builder = StringBuilder()
            builder.append("[")
            this.forEachIndexed { index, any ->
                if (index > 0) {
                    builder.append(", ")
                }
                builder.append(any?.logValue()?:"null")
            }
            builder.append("]")
            return builder.toString()
        }

        is Iterable<*> -> {
            val builder = StringBuilder()
            builder.append("[")
            this.forEachIndexed { index, any ->
                if (index > 0) {
                    builder.append(", ")
                }
                builder.append(any?.logValue()?:"null")
            }
            builder.append("]")
            return builder.toString()
        }

        else -> {
            return this.toString()
        }

    }
}

inline fun <reified T : View> View.findView(idName: String): T? {
    return findViewById(context.getId(idName))
}

inline fun <reified T : View> Activity.findView(idName: String): T? {
    return findViewById(getId(idName))
}

fun Any.findMethod(method: String, vararg parameterTypes: Class<*>): Method? {
    return findMethod(this::class.java, method, *parameterTypes)
}

fun ClassLoader.findMethod(
    clazz: String,
    method: String,
    vararg parameterTypes: Class<*>
): Method? {
    return findMethod(find(clazz), method, *parameterTypes)
}

fun ClassLoader.findConstructor(
    clazz: String,
    vararg parameterTypes: Class<*>
): Constructor<*>? {
    return findConstructor(find(clazz), *parameterTypes)
}

private fun findConstructor(clazz: Class<*>, vararg parameterTypes: Class<*>): Constructor<*>? {
    try {
        val declaredConstructor = clazz.getDeclaredConstructor(*parameterTypes)
        declaredConstructor.isAccessible = true
        return declaredConstructor
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return null
}

private fun findMethod(clazz: Class<*>, method: String, vararg parameterTypes: Class<*>): Method? {
    val classList = LinkedList<Class<*>>()
    classList.add(clazz)
    while (classList.isNotEmpty()) {
        val c = classList.removeFirst()
        try {
            val declaredMethod = c.getDeclaredMethod(method, *parameterTypes)
            declaredMethod.isAccessible = true
            return declaredMethod
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        c.superclass?.let {
            classList.addLast(it)
        }
        c.interfaces.forEach {
            classList.addLast(it)
        }
    }
    return null
}

fun Context.getId(idName: String): Int {
    return resources.getIdentifier(
        idName,
        "id",
        packageName
    )
}

fun interface OnMethodBeforeCallback {
    fun onMethodBefore(param: MethodParam)
}

fun interface OnMethodAfterCallback {
    fun onMethodAfter(param: MethodParam)
}

class DefaultHookCallback : XC_MethodHook() {

    private var onBeforeCallback: OnMethodBeforeCallback? = null
    private var onAfterCallback: OnMethodAfterCallback? = null

    fun onBefore(callback: OnMethodBeforeCallback) {
        this.onBeforeCallback = callback
    }

    fun onAfter(callback: OnMethodAfterCallback) {
        this.onAfterCallback = callback
    }

    override fun beforeHookedMethod(param: MethodHookParam?) {
        param ?: return
        this.onBeforeCallback?.onMethodBefore(MethodParam(param))
    }

    override fun afterHookedMethod(param: MethodHookParam?) {
        param ?: return
        this.onAfterCallback?.onMethodAfter(MethodParam(param))
    }
}

class MethodParam(private val realParam: XC_MethodHook.MethodHookParam) {
    var method: Member?
        get() {
            return realParam.method
        }
        set(value) {
            realParam.method = value
        }

    var thisObject: Any?
        get() {
            return realParam.thisObject
        }
        set(value) {
            realParam.thisObject = value
        }

    var args: Array<Any?>?
        get() {
            return realParam.args
        }
        set(value) {
            realParam.args = value
        }

    var result: Any?
        get() {
            return realParam.result
        }
        set(value) {
            realParam.result = value
        }
    var throwable: Throwable?
        get() {
            return realParam.throwable
        }
        set(value) {
            realParam.throwable = value
        }

    val hasThrowable: Boolean
        get() {
            return realParam.hasThrowable()
        }

    fun getResultOrThrowable(): Any? {
        return realParam.resultOrThrowable
    }

}

class PackageParam(private val realParam: XC_LoadPackage.LoadPackageParam) {
    var packageName: String
        get() {
            return realParam.packageName
        }
        set(value) {
            realParam.packageName = value
        }

    var processName: String?
        get() {
            return realParam.processName
        }
        set(value) {
            realParam.processName = value
        }

    var classLoader: ClassLoader?
        get() {
            return realParam.classLoader
        }
        set(value) {
            realParam.classLoader = value
        }

    var appInfo: ApplicationInfo?
        get() {
            return realParam.appInfo
        }
        set(value) {
            realParam.appInfo = value
        }

    var isFirstApplication: Boolean
        get() {
            return realParam.isFirstApplication
        }
        set(value) {
            realParam.isFirstApplication = value
        }
}

fun Any.log(vararg value: Any) {
    val logValue = value.map { it.toString() }.toTypedArray().contentToString()
    Log.d("LollipopXposed ", " ${this::class.java.simpleName} : $logValue")
}

fun Any.xLog(vararg value: Any) {
    try {
        val logValue = value.map { it.toString() }.toTypedArray().contentToString()
        XposedBridge.log("LollipopXposed : ${this::class.java.simpleName} : $logValue")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}