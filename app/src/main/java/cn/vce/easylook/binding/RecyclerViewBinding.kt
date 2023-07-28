package cn.vce.easylook.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.feature_music.other.Resource
import cn.vce.easylook.feature_music.other.Status
import com.drake.brv.PageRefreshLayout
import com.drake.brv.utils.bindingAdapter
import com.drake.statelayout.StateLayout

object RecyclerViewBinding {

    @JvmStatic
    @BindingAdapter(value =  ["submitList", "submitRList", "fetchScroFirst", "singCheckPosition"], requireAll = false)
    fun bind(view: RecyclerView,
             itemList: List<Any>?,
             itemRList: Resource<List<Any>?>?,
             fetchScroFirst: Boolean?,
             singCheckPosition: Int?) {
        val adapter = view.bindingAdapter

        //回到第一行,一开始就不存在数据，现在才有的情况下才起作用
        if (fetchScroFirst == true && adapter.models?.isEmpty() != false && itemList?.isNotEmpty() != false) {
            when (val layoutManager = view.layoutManager) {
                is LinearLayoutManager -> {
                    val firstVisibleItemPosition: Int =
                        layoutManager.findFirstVisibleItemPosition()
                    if (firstVisibleItemPosition != 0) {
                        //刷新数据锁定第一项
                        layoutManager.scrollToPositionWithOffset(0, 0)
                    }
                }
            }
        }
        if (itemRList == null){
            adapter.models = itemList
            when(val parentView = view.parent){
                is StateLayout -> {
                    when(val parentView = parentView.parent) {
                        is PageRefreshLayout -> {
                            val models = adapter.models
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
        }else{
            adapter.models = itemRList.data
            val parentView = view.parent.parent
            val isPageFlag = view.parent.parent is PageRefreshLayout
            if (isPageFlag) {
                parentView as PageRefreshLayout
                when (itemRList.status) {
                    Status.SUCCESS -> {
                        if (itemRList.data == null) {
                            parentView.showEmpty()
                        }else{
                            parentView.showContent()
                        }
                    }
                    Status.LOADING -> {
                        parentView.showLoading()
                    }
                    Status.ERROR -> {
                        parentView.showError()
                    }
                }
            }
        }

        val models = adapter.models
        //以下都需要models ！= null
        models?.let{
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
