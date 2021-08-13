package com.lollipop.qin1sptools.activity.games

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lollipop.qin1sptools.R
import com.lollipop.qin1sptools.activity.FileChooseActivity
import com.lollipop.qin1sptools.activity.base.GridMenuActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()

        requestStoragePermissions()

        val thisIntent = intent
        val uri = thisIntent.data
        if (thisIntent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == 0
            && savedInstanceState == null && uri != null) {
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
//        Config.startApp(this, gameInfo.title, gameInfo.pathExt)
        MicroDisplayActivity.start(this, gameInfo.title, gameInfo.pathExt)
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

}