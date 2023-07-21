package cn.vce.easylook.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.utils.LogE
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.bindingAdapter
import com.drake.statelayout.StateLayout

object RecyclerViewBinding {

    @JvmStatic
    @BindingAdapter(value =  ["submitList", "fetchScroFirst", "singCheckPosition"], requireAll = false)
    fun bind(view: RecyclerView,
             itemList: List<Any>?,
             fetchScroFirst: Boolean?,
             singCheckPosition: Int?) {
        val adapter = view.bindingAdapter

        adapter.models = itemList
        val models = adapter.models

        when(val parentView = view.parent){
            is StateLayout -> {
                when(val parentView = parentView.parent) {
                    is PageRefreshLayout -> {
                        if (models == null) {
                            parentView.showError(force = true)
                        }else if (models.isEmpty()) {
                            parentView.showEmpty()
                        }else{
                            parentView.showContent()
                        }
                    }
                }
            }
        }
        //以下都需要models ！= null
        models?.let{
            //回到第一行
            if (fetchScroFirst == true){
                when(val layoutManager = view.layoutManager){
                    is LinearLayoutManager -> {
                        val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                        if (firstVisibleItemPosition != 0){
                            //刷新数据锁定第一项
                            layoutManager.scrollToPositionWithOffset(0, 0)
                        }
                    }
                }
            }

            //单选情况下记录选中条目
            if (adapter.singleMode){
                if (singCheckPosition !=null && singCheckPosition >= 0) {
                    if (models.size > singCheckPosition) {
                        adapter.setChecked(singCheckPosition, true)
                    } else if (models.isNotEmpty()) {
                        adapter.setChecked(0, true)
                    }
                }else if (models.isNotEmpty()) {
                    adapter.setChecked(0, true)
                }
            }
        }

    }

    @JvmStatic
    @BindingAdapter(value =  ["pageSubmit"], requireAll = false)
    fun pageSubmit(view: PageRefreshLayout, any: Resource<*>?) {
        any?.run {
            when(any.status){
                Status.SUCCESS -> {
                    val data = any.data
                    if (data is List<*>){
                        if (data.isEmpty()){
                            view.showEmpty()
                        }else{
                            view.index = 1
                            view.addData(data)
                        }
                    }else{
                        view.showError()
                    }
                }
                Status.LOADING -> {
                    view.showLoading()
                }
                Status.ERROR -> {
                    view.showError()
                }
            }
        }
    }

}
