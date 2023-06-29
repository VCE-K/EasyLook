package cn.vce.easylook.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import cn.vce.easylook.EasyApp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.util.*


const val DEFAULT_TAG = "atri_tag"

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

/*fun String.toast(duration: Int = Toast.LENGTH_SHORT) {
    if (TextUtils.isEmpty(this))return
    EasyApp.context.toast(this, duration)
}*/

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

// about views
fun View.setShadowBackground(bgDrawable: Drawable) {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    ViewCompat.setBackground(this, bgDrawable)
}

inline fun <R> TypedArray.safeUse(block: TypedArray.() -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        Log.e(DEFAULT_TAG, "Error occurred while use typed array...${e.message}")
        null
    } finally {
        recycle()
    }
}

// ConstraintSet apply 到同一个 view 上
inline fun ConstraintSet.applyToTheSame(
    view: ConstraintLayout,
    block: ConstraintSet.() -> Unit
) {
    this.apply { clone(view) }.apply {
        block()
    }.applyTo(view)
}

fun <T> unlockLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.PUBLICATION, initializer)
//endregion

//region About list
fun <T> MutableList<T>.swap(first: Int, second: Int) {
    val tmp = this[first]
    this[first] = this[second]
    this[second] = tmp
}



fun Any.LogE(msg: String, tag: String? = null, t: Throwable? = null) {
    Log.e(tag ?: javaClass.simpleName, msg ?: "", t)
}

fun <T> convertList(originalList: List<*>?, targetTypeClass: Class<T>?): List<T> {
    val gson = Gson()
    //val jsonStr = gson.toJson(originalList)
    val gsonBuilder = GsonBuilder()
    val jsonString: String = gsonBuilder.serializeNulls().create().toJson(originalList)
    val targetType: Type = TypeToken.getParameterized(MutableList::class.java, targetTypeClass).type
    return gson.fromJson(jsonString, targetType)
}

fun <T> convertObject(originalObject: Any?, targetTypeClass: Class<T>?): T {
    val gson = Gson()
    val gsonBuilder = GsonBuilder()
    val jsonString: String = gsonBuilder.serializeNulls().create().toJson(originalObject)
    return gson.fromJson(jsonString, targetTypeClass)
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