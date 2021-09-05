package com.lollipop.qin1sptools.activity.games

import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.FileChooseActivity
import com.lollipop.qin1sptools.activity.base.GridMenuActivity
import com.lollipop.qin1sptools.dialog.MessageDialog
import com.lollipop.qin1sptools.dialog.OptionDialog
import com.lollipop.qin1sptools.event.KeyEvent
import com.lollipop.qin1sptools.guide.Guide
import com.lollipop.qin1sptools.utils.FeatureIcon
import com.lollipop.qin1sptools.utils.doAsync
import com.lollipop.qin1sptools.utils.onUI
import com.lollipop.qin1sptools.utils.requestStoragePermissions
import ru.playsoftware.j2meloader.applist.AppItem
import ru.playsoftware.j2meloader.appsdb.AppRepository
import ru.playsoftware.j2meloader.config.Config
import ru.playsoftware.j2meloader.util.AppUtils
import ru.playsoftware.j2meloader.util.JarConverter
import java.io.File
import javax.microedition.util.ContextHolder

class J2meActivity : GridMenuActivity() {

    companion object {
        private const val JAR_FILTER = ".*\\.[jJ][aA][rRdD]\$"
    }

    override val baseFeatureIconArray = arrayOf(
        FeatureIcon.ADD,
        FeatureIcon.NONE,
        FeatureIcon.BACK
    )

    private val gameList = ArrayList<AppItem>()

    private val appRepository by lazy {
        AppRepository(this)
    }

    private val converter by lazy {
        JarConverter(applicationInfo.dataDir)
    }

    private var deleteDialog: MessageDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()

        requestStoragePermissions()

        val thisIntent = intent
        val uri = thisIntent.data
        if (thisIntent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == 0
            && savedInstanceState == null && uri != null
        ) {
            decodeByIntent(uri)
        }
    }

    private fun initData() {
        gridItemList.clear()
        notifyDataSetChanged()
        startLoading()
        doAsync(::onError) {
            updateGameList()
            onUI {
                endLoading()
                notifyDataSetChanged()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            decodeByIntent(uri)
        }
    }

    private fun decodeByIntent(uri: Uri) {
        convertJar(uri)
    }

    private fun updateGameList() {
        val apps = appRepository.all
        AppUtils.updateDb(appRepository, apps)

        val defaultIcon = ContextCompat.getDrawable(this, R.mipmap.ic_launcher_java)!!

        val menuList = ArrayList<GridItem>()
        apps.forEach { app ->
            try {
                menuList.add(
                    GridItem(
                        app.id,
                        Drawable.createFromPath(app.imagePathExt) ?: defaultIcon,
                        app.title
                    )
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        gridItemList.clear()
        gridItemList.addAll(menuList)
        gameList.clear()
        gameList.addAll(apps)
    }

    private fun onError(error: Throwable) {
        error.printStackTrace()
        endLoading()
        gridItemList.clear()
        setTitle(R.string.error)
        notifyDataSetChanged()
    }

    override fun onGridItemClick(item: GridItem, index: Int) {
        val id = item.id
        val gameInfo = gameList.find { it.id == id } ?: return
        updateWindowInsets()
        MicroDisplayActivity.start(
            this,
            gameInfo.title,
            gameInfo.pathExt,
            ContextHolder.getStatusBarSize()
        )
    }

    override fun onKeyUp(event: KeyEvent, repeatCount: Int): Boolean {
        if (event == KeyEvent.KEY_POUND) {
            if (callDeleteApp()) {
                return true
            }
        }
        return super.onKeyUp(event, repeatCount)
    }

    private fun callDeleteApp(): Boolean {
        if (deleteDialog != null) {
            return true
        }
        val selectedItem = getSelectedItem() ?: return false
        val gameInfo = gameList.find { it.id == selectedItem.id } ?: return false
        deleteDialog = MessageDialog.build(this) {
            message = getString(R.string.dialog_msg_delete_java_game, gameInfo.title)
            setLeftButton(R.string.delete) {
                AppUtils.deleteApp(gameInfo)
                it.dismiss()
                deleteDialog = null
            }
            setRightButton(R.string.cancel) {
                it.dismiss()
                deleteDialog = null
            }
        }
        deleteDialog?.show()
        return true
    }

    private fun updateWindowInsets() {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        ContextHolder.setStatusBarSize(rectangle.top)
    }

    override fun onGridItemInfoClick(item: GridItem?, index: Int) {
        if (item == null) {
            FileChooseActivity.start(this, rootDir = Config.getEmulatorDir())
        } else {
            val id = item.id
            val gameInfo = gameList.find { it.id == id } ?: return
            FileChooseActivity.start(this, rootDir = gameInfo.pathExt)
        }
    }

    override fun onLeftFeatureButtonClick(): Boolean {
        FileChooseActivity.start(this, filter = JAR_FILTER, chooseFile = true)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (FileChooseActivity.isResult(requestCode)) {
            val resultFile = FileChooseActivity.getResultFile(resultCode, data)
            if (resultFile != null) {
                onUI {
                    convertJar(resultFile)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun convertJar(uri: Uri) {
        startLoading()
        setFeatureButtons(FeatureIcon.NONE, FeatureIcon.NONE, FeatureIcon.NONE)
        doAsync({
            showToast(R.string.convert_error)
            endLoading()
        }) {
            val gameDir = converter.convert(uri)
            val app = AppUtils.getApp(gameDir)
            appRepository.insert(app)
            updateGameList()
            onUI {
                endLoading()
                notifyDataSetChanged()
                setFeatureButtons()
            }
        }
    }

    private fun convertJar(file: File) {
        convertJar(Uri.fromFile(file))
    }

    override fun buildGuide(builder: Guide.Builder) {
        builder.next(KeyEvent.OPTION, R.string.guide_add_jar)
            .next(KeyEvent.KEY_5, R.string.guide_grid_num)
        super.buildGuide(builder)
    }

}