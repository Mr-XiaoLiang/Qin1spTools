package com.lollipop.qin1sptools.utils

import android.Manifest
import android.app.Activity
import android.os.Build

/**
 * @author lollipop
 * @date 2021/8/13 21:46
 */
object PermissionsHelper {

    const val REQUEST_CODE_PERMISSIONS = 996

}
inline fun <reified T: Activity> T.requestPermissions(vararg names: String) {
    if (versionThen(Build.VERSION_CODES.M)) {
        requestPermissions(names, PermissionsHelper.REQUEST_CODE_PERMISSIONS)
    }
}

inline fun <reified T: Activity> T.requestStoragePermissions() {
    if (versionThen(Build.VERSION_CODES.R)) {
        requestPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )
    } else {
        requestPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}