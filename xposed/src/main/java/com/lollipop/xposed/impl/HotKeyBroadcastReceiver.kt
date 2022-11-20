package com.lollipop.xposed.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class HotKeyBroadcastReceiver(private val callback: OnHotKeyDownCallback) : BroadcastReceiver() {

    companion object {

        private const val HOT_KEY_DOWN_ACTION = "com.lollipop.xposed.HotKey"
        private const val PARAM_HOT_KEY_CODE = "HOT_KEY_CODE"
        private const val PARAM_HOT_KEY_PKG = "HOT_KEY_PKG"

        fun send(context: Context, keyCode: Int, pkg: String) {
            context.sendBroadcast(
                Intent(HOT_KEY_DOWN_ACTION)
                    .putExtra(PARAM_HOT_KEY_CODE, keyCode)
                    .putExtra(PARAM_HOT_KEY_PKG, pkg)
            )
        }
    }

    fun register(context: Context) {
        context.registerReceiver(this, IntentFilter(HOT_KEY_DOWN_ACTION))
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        if (intent.action == HOT_KEY_DOWN_ACTION) {
            val keyCode = intent.getIntExtra(PARAM_HOT_KEY_CODE, 0)
            if (keyCode == 0) {
                return
            }
            val pkg = intent.getStringExtra(PARAM_HOT_KEY_PKG) ?: return
            onHotKeyDown(keyCode, pkg)
        }
    }

    private fun onHotKeyDown(keyCode: Int, pkg: String) {
        callback.onHotKeyDown(keyCode, pkg)
    }

    fun interface OnHotKeyDownCallback {
        fun onHotKeyDown(keyCode: Int, pkg: String)
    }

}