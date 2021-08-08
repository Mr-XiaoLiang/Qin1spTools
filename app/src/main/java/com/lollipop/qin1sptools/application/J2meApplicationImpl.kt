package com.lollipop.qin1sptools.application

import android.app.Application
import javax.microedition.util.ContextHolder

/**
 * @author lollipop
 * @date 2021/8/8 12:56
 */
class J2meApplicationImpl: ApplicationImpl {

    override fun onCreate(application: Application) {
        ContextHolder.setApplication(application)
    }

}