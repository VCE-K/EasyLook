package cn.vce.easylook.binding

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter

object EditTextBinding {

    //文字变化执行方法，正常用来和etSearchText配合检索
    @JvmStatic
    @BindingAdapter("doAfterTextChanged")
    fun doAfterTextChanged(view: EditText, action: () -> Unit) {
        view.doAfterTextChanged {
            action()
        }
    }

    @JvmStatic
    @BindingAdapter("text")
    fun setText(view: EditText, text: String?) {
        val oldInput = view.text.toString()
        if (!haveContentsChanged(text, oldInput)) {
            return // 数据没有变化不进行刷新视图
        }
        view.setText(text)
    }

    // 本工具类截取自官方源码
    private fun haveContentsChanged(str1: String?, str2: String): Boolean {
        if ((str1 == null) != (str2 == null)) {
            return true
        } else if (str1 == null) {
            return false
        }
        val length: Int = str1.length
        if (length != str2.length) {
            return true
        }

        for (i in 0..length) {
            if (str1[i] != str2[i]) {
                return true
            }
        }
        return false
    }
}