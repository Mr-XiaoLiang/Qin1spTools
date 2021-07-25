package com.lollipop.qin1sptools.utils

/**
 * @author lollipop
 * @date 2021/7/25 18:06
 * 操作计数器
 */
class ActionIndexer {

    private var index = 0

    val now: Int
        get() {
            return index
        }

    fun newAction() {
        index++
    }

    fun active(action: Int): Boolean {
        return now == action
    }

}