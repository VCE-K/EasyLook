package cn.vce.easylook.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.vce.easylook.EasyApp
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.bli.download.DownloadInfoResponse
import cn.vce.easylook.feature_music.other.DownloadResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


//region Get value from XML
fun Context.getDimenInt(@DimenRes resId: Int): Int = resources.getDimension(resId).toInt()

fun Context.getDimenFloat(@DimenRes resId: Int): Float = resources.getDimension(resId)

fun Context.getColorRes(@ColorRes resColor: Int): Int =
    ResourcesCompat.getColor(resources, resColor, theme)

fun Context.getDrawableRes(@DrawableRes resDrawable: Int): Drawable? =
    ResourcesCompat.getDrawable(resources, resDrawable, theme)

fun View.getColorRes(@ColorRes resColor: Int) = context.getColorRes(resColor)
//endregion

//region To dp, sp, px
val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Float.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

val Float.px
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this,
        Resources.getSystem().displayMetrics
    )

val Float.px2Dp: Float
    get() {
        val density = Resources.getSystem().displayMetrics.density
        return (this / density + 0.5).toFloat()
    }

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Int.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Int.px
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Int.px2Dp: Int
    get() {
        val density = Resources.getSystem().displayMetrics.density
        return (this / density + 0.5).toInt()
    }
val Int.dp2Px: Int
    get() {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getXScreenWidth(x: Int): Int {
    val mScreenWidth = getScreenWidth()
    return (mScreenWidth - x.dp2Px) * 9 / 16
}
//endregion

//region Common tools
// toast

/**
 * 获取资源文件中定义的字符串。
 *
 * @param resId
 * 字符串资源id
 * @return 字符串资源id对应的字符串内容。
 */
fun getString(resId: Int): String = EasyApp.context.resources.getString(resId)


fun toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (TextUtils.isEmpty(content))return
    EasyApp.context.toast(content, duration)
}

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT, showApp: Boolean = false) {
    if (showApp) {
        toast(msg, duration)
    } else {
        Toast.makeText(this, null, duration).apply {
            setText(msg)
        }.show()
    }

}

fun Context.toast(msgRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msgRes, duration).show()
}



// 流转字符串
fun InputStream.convertToString(): String {
    var s = ""
    try {
        val scanner = Scanner(this, Charsets.UTF_8.name()).useDelimiter("\\A")
        if (scanner.hasNext()) {
            s = scanner.next()
        }
    } catch (e: IOException) {
        throw e
    } finally {
        close()
    }
    return s
}

fun Any.LogE(msg: String, tag: String? = null, t: Throwable? = null) {
    Log.e(tag ?: javaClass.simpleName, msg ?: "", t)
}


fun convertMusicList(songs: List<*>?, source: String): List<MusicInfo>?{
    return convertList(songs, MusicInfo::class.java)?.map{
        it.pid = ""
        it.source = source
        it
    }
}


fun <T> convertList(originalList: List<*>?, targetTypeClass: Class<T>?): List<T>? {
    try {
        val gson = Gson()
        //val jsonStr = gson.toJson(originalList)
        val gsonBuilder = GsonBuilder()
        val jsonString: String = gsonBuilder.serializeNulls().create().toJson(originalList)
        val targetType: Type = TypeToken.getParameterized(MutableList::class.java, targetTypeClass).type
        return gson.fromJson(jsonString, targetType)
    }catch (e: NullPointerException){
        e.printStackTrace()
    }
    return null
}

fun <T> convertObject(originalObject: Any?, targetTypeClass: Class<T>?): T? {
    try {
        val gson = Gson()
        val gsonBuilder = GsonBuilder()
        val jsonString: String = gsonBuilder.serializeNulls().create().toJson(originalObject)
        return gson.fromJson(jsonString, targetTypeClass)
    }catch (e: NullPointerException){
        e.printStackTrace()
    }
    return null
}


//视频所用

/**
 * 隐藏view
 */
fun View?.gone() {
    this?.visibility = View.GONE
}
fun View?.visible() {
    this?.visibility = View.GONE
}

//字体
val fzlLTypeface by lazy {
    Typeface.createFromAsset(EasyApp.context.assets, "fonts/FZLanTingHeiS-L-GB-Regular.TTF")
}

val fzdb1Typeface by lazy {
    Typeface.createFromAsset(EasyApp.context.assets, "fonts/FZLanTingHeiS-DB1-GB-Regular.TTF")
}

val futuraTypeface by lazy {
    Typeface.createFromAsset(EasyApp.context.assets, "fonts/Futura-CondensedMedium.ttf")
}

val dinTypeface by lazy {
    Typeface.createFromAsset(EasyApp.context.assets, "fonts/DIN-Condensed-Bold.ttf")
}

val lobsterTypeface by lazy {
    Typeface.createFromAsset(EasyApp.context.assets, "fonts/Lobster-1.4.otf")
}


fun getTimeParam(offset: Int): String {
    val sdf = SimpleDateFormat("yyyyMMdd")
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1 - offset) //1 for 时区
    return sdf.format(calendar.time)?:""
}

/**
 * 解析xml布局
 *
 * @param parent 父布局
 * @param attachToRoot 是否依附到父布局
 */
fun Int.inflate(parent: ViewGroup, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(parent.context).inflate(this, parent, attachToRoot)
}


//文件相关
fun getReadFileName(filename: String, contentLength: Long): DownloadResult<File> {
    var file = File("${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_MUSIC}", filename)
    var downloadLength = 0L
    if (file.exists()) {
        downloadLength = file.length()
    }
    var index = 0
    var filenameOther = ""
    while(downloadLength >= contentLength){
        val doIndex = filename.lastIndexOf(".")
        if (doIndex == -1){
            filenameOther = filename + "(${index})"
        }else{
            var mime = filename.substring(doIndex)
            filenameOther = filename.substring(0, doIndex) + "(${index})" + mime
        }
        val newFile = File("${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_MUSIC}", filenameOther)
        downloadLength = newFile.length()
        file = newFile
        index++
    }
    return DownloadResult.loading<File>(
        downloadLength,
        contentLength,
        downloadLength.toFloat() / contentLength.toFloat(),
        file.name
    )
}


fun Activity.hideSoftInput() {
    currentFocus?.let {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    } ?: let {
        ViewCompat.getWindowInsetsController(window.decorView)?.hide(WindowInsetsCompat.Type.ime())
    }
}