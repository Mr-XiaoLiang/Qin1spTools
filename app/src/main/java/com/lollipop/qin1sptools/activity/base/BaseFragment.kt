package com.lollipop.qin1sptools.activity.base

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * @author lollipop
 * @date 2021/9/20 19:18
 */
open class BaseFragment: Fragment() {

    protected inline fun <reified T> identityCheck(c: Context?, callback: (T) -> Unit) {
        parentFragment?.let {
            if (it is T) {
                callback(it)
                return
            }
        }
        context?.let {
            if (it is T) {
                callback(it)
                return
            }
        }
        c?.let {
            if (it is T) {
                callback(it)
                return
            }
        }
    }

}