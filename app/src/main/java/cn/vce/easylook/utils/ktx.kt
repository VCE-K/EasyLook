package cn.vce.easylook.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
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
import java.io.IOException
import java.io.InputStream
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



fun Any.LogE(msg: String?, tag: String? = null, t: Throwable? = null) {
    Log.e(tag ?: javaClass.simpleName, msg ?: "", t)
}

