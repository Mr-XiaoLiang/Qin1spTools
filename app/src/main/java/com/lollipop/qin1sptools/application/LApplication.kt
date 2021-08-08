package com.lollipop.qin1sptools.application

import android.app.Application
import androidx.multidex.MultiDex

/**
 * @author lollipop
 * @date 2021/8/8 12:55
 */
class LApplication : Application() {

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