package com.lollipop.qin1sptools.activity

import android.os.Bundle
import android.os.Environment
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.utils.ActionIndexer
import com.lollipop.qin1sptools.utils.FeatureIcon
import com.lollipop.qin1sptools.utils.doAsync
import com.lollipop.qin1sptools.utils.onUI
import java.io.File
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * 文件选择的Activity
 * 可以按照要求选中一个文件夹或者某个文件
 * @author Lollipop
 * @date 2021/07/23
 */
class FileChooseActivity : SimpleListActivity() {

    companion object {
        private const val KEY_FILE_FILTER = "KEY_FILE_FILTER"
        private const val KEY_CHOOSE_FILE = "KEY_CHOOSE_FILE"
    }

    override val baseFeatureIconArray = arrayOf(
        FeatureIcon.ADD,
        FeatureIcon.SELECT,
        FeatureIcon.BACK
    )

    private val breadCrumbs = LinkedList<DirInfo>()

    private val currentFiles = ArrayList<String>()

    private val actionIndexer = ActionIndexer()

    private val fileFilter by lazy {
        intent.getStringExtra(KEY_FILE_FILTER)
    }

    private val chooseFile by lazy {
        intent.getBooleanExtra(KEY_CHOOSE_FILE, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {

    }

    private fun initData() {
        //获取存储状态
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            setError(getString(R.string.external_storage_not_found))
            return
        }
        //获取根目录
        next(Environment.getExternalStorageDirectory())
    }

    private fun next(dir: File) {
        if (!dir.canRead() || !dir.canWrite()) {
            if (breadCrumbs.isEmpty()) {
                setError(getString(R.string.external_storage_not_found))
                return
            }
            setError(getString(R.string.folder_unavailable))
            return
        }
        actionIndexer.newAction()
        clearError()
        currentFiles.clear()
        startLoading()
        updateData()
        doAsync {
            val nowAction = actionIndexer.now
            val pattern: Pattern? = fileFilter?.let { Pattern.compile(it) }
            val childrenList = ArrayList<String>()
            val childrenFileList = ArrayList<File>()
            if (dir.isDirectory) {
                dir.listFiles()?.forEach {
                    var needAdd = false
                    if (it.isDirectory) {
                        needAdd = true
                    } else if (pattern != null) {
                        if (pattern.matcher(it.name).matches()) {
                            needAdd = true
                        }
                    } else {
                        needAdd = true
                    }
                    if (needAdd) {
                        childrenList.add(it.name)
                        childrenFileList.add(it)
                    }
                }
            }
            if (actionIndexer.active(nowAction)) {
                breadCrumbs.addLast(DirInfo(dir, childrenList, childrenFileList))
                currentFiles.addAll(childrenList)
                onUI {
                    updateData()
                    endLoading()
                }
            }
        }
    }

    private fun back(): Boolean {
        if (breadCrumbs.isEmpty()) {
            return false
        }
        breadCrumbs.removeLast()
        if (breadCrumbs.isEmpty()) {
            return false
        }
        actionIndexer.newAction()
        val last = breadCrumbs.last
        currentFiles.clear()
        currentFiles.addAll(last.children)
        updateData()
        return true
    }

    private fun updateData() {
        setData(currentFiles)
    }

    override fun onKeyUp(event: KeyEvent): Boolean {
        if (isLoading && event == KeyEvent.BACK) {
            back()
        }
        return super.onKeyUp(event)
    }

    override fun onCenterFeatureButtonClick(): Boolean {
        val index = selectedIndex
        if (index >= 0 && index < currentFiles.size) {
            val last = breadCrumbs.last ?: return true
            val childrenFile = last.childrenFile
            if (childrenFile.size == currentFiles.size) {
                next(childrenFile[index])
                return true
            }
        }
        return super.onCenterFeatureButtonClick()
    }

    override fun onRightFeatureButtonClick(): Boolean {
        return back() || super.onRightFeatureButtonClick()
    }

    private class DirInfo(
        val file: File,
        val children: List<String>,
        val childrenFile: List<File>
    )

}