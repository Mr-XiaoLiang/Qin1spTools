package com.lollipop.qin1sptools.application

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

/**
 * @author lollipop
 * @date 2021/8/8 12:55
 */
class LApplication : MultiDexApplication() {

    companion object {
        private val IMPL_ARRAY: Array<Class<out ApplicationImpl>> = arrayOf(
            J2meApplicationImpl::class.java
        )
    }

    private val implArray = IMPL_ARRAY.map { it.newInstance() }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        implArray.forEach {
            it.onCreate(this)
        }
    }

}