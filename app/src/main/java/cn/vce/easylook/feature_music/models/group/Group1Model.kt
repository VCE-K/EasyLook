package cn.vce.easylook.feature_music.models.group

import androidx.databinding.BaseObservable
import androidx.room.Ignore
import cn.vce.easylook.R
import cn.vce.easylook.feature_music.models.MusicInfo
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemHover
import com.drake.brv.item.ItemPosition

open class Group1Model : ItemExpand, ItemHover, ItemPosition,
    BaseObservable() {
    @Ignore
    override var itemGroupPosition: Int = 0
    @Ignore
    override var itemExpand: Boolean = false
        set(value) {
            field = value
            notifyChange()
        }

    /** 由于类型是List<Any>所以本字段不能用于json解析, 所以使用真实字段jsonSublist代理 */
    @get:Ignore
    override var itemSublist: List<Any?>?
        get() = jsonSublist
        set(value) {
            jsonSublist = value as List<MusicInfo> // 注意类型转换异常
        }

    /** 接口数据里面的子列表使用此字段接收(请注意避免gson等框架解析kotlin会覆盖字段默认值问题) */
    @Ignore
    private var jsonSublist: List<MusicInfo>? = null
    @Ignore
    override var itemHover: Boolean = true
    @Ignore
    override var itemPosition: Int = 0
    @get:Ignore
    open val title
        get() = "分组 [ ${jsonSublist?.size?: 0} ]"
    @get:Ignore
    val expandIcon get() = if (itemExpand) R.drawable.ic_arrow_expand else R.drawable.ic_arrow_collapse

}