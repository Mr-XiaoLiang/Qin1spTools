package com.lollipop.qin1sptools.activity.games

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.base.SimpleListActivity
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.guide.Guide
import com.lollipop.qin1sptools.utils.FeatureIcon
import com.lollipop.qin1sptools.utils.doAsync
import com.lollipop.qin1sptools.utils.onUI
import java.io.*

class PresetJarActivity : SimpleListActivity() {

    companion object {
        private const val DIR = "jars"
        private const val REQUEST_CODE = 0xF2e
        private const val KEY_SELECTED_FILE = "KEY_SELECTED_FILE"

        fun isResult(requestCode: Int): Boolean {
            return REQUEST_CODE == requestCode
        }

        fun start(
            activity: Activity,
            requestCode: Int = REQUEST_CODE,
        ) {
            activity.startActivityForResult(
                Intent(activity, PresetJarActivity::class.java),
                requestCode
            )
        }

        fun getResultFile(resultCode: Int, intent: Intent?): File? {
            if (resultCode != RESULT_OK || intent == null) {
                return null
            }
            val file = intent.getSerializableExtra(KEY_SELECTED_FILE) ?: return null
            if (file is File) {
                return file
            }
            return null
        }

        private fun setResultFile(activity: Activity, file: File) {
            activity.setResult(RESULT_OK, Intent().apply {
                putExtra(KEY_SELECTED_FILE, file)
            })
        }

    }

    override val baseFeatureIconArray = arrayOf(
        FeatureIcon.NONE,
        FeatureIcon.SELECT,
        FeatureIcon.BACK
    )

    private val currentFiles = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        doAsync {
            val jarList = assets.list(DIR)
            currentFiles.clear()
            jarList?.forEach {
                currentFiles.add(it)
            }
            onUI {
                updateData()
                endLoading()
            }
        }
    }

    private fun updateData() {
        setData(currentFiles)
    }

    override fun onCenterFeatureButtonClick(): Boolean {
        choose()
        return true
    }

    private fun choose() {
        if (currentFiles.isEmpty()) {
            return
        }
        val index = selectedIndex
        if (currentFiles.size <= index) {
            return
        }
        val fileName = currentFiles[index]
        startLoading()
        doAsync({
            endLoading()
            showToast(R.string.error)
        }) {
            val outFile = File(cacheDir, "preset.jar")
            if (outFile.exists()) {
                outFile.delete()
            }
            copy(assets.open("$DIR/$fileName"), FileOutputStream(outFile))
            onUI {
                setResultFile(this, outFile)
                finish()
            }
        }
    }

    private fun copy(inputStream: InputStream, outputStream: OutputStream) {
        try {
            val buffer = ByteArray(2048)
            do {
                val readCount = inputStream.read(buffer)
                if (readCount < 0) {
                    break
                }
                outputStream.write(buffer, 0, readCount)
            } while (true)
            outputStream.flush()
        } finally {
            inputStream.tryClose()
            outputStream.tryClose()
        }
    }

    private fun Closeable.tryClose() {
        try {
            this.close()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun buildGuide(builder: Guide.Builder) {
        builder.next(KeyEvent.UP, R.string.guide_file_up)
            .next(KeyEvent.DOWN, R.string.guide_file_down)
            .next(KeyEvent.CENTER, R.string.guide_file_center)
            .next(KeyEvent.BACK, R.string.guide_file_back)
        super.buildGuide(builder)
    }

}