package cn.vce.easylook.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.vce.easylook.utils.LogE
import com.drake.brv.utils.bindingAdapter

object RecyclerViewBinding {

    @JvmStatic
    @BindingAdapter(value =  ["submitList"], requireAll = false)
    fun bindSubmitList(view: RecyclerView, itemList: List<Any>?) {
        val adapter = (view.adapter as? com.drake.brv.BindingAdapter) ?: kotlin.run {
            LogE("The adapter of RecyclerView isn`t ListAdapter")
            return
        }
        if (adapter.models != itemList){
            adapter.models = itemList
        }
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


    @JvmStatic
    @BindingAdapter(value =  ["singleCheck", "checkPosition"], requireAll = false)
    fun firstSingleCheck(view: RecyclerView, itemList: List<Any>?, checkPosition: Int = 0) {
        val adapter = view.bindingAdapter
        if (adapter.singleMode){
            itemList?.apply {
                if (itemList.size > checkPosition){
                    when(val layoutManager = view.layoutManager) {
                        is LinearLayoutManager -> {
                            layoutManager.scrollToPosition(checkPosition)
                        }
                    }
                    view.bindingAdapter.setChecked(checkPosition, true)
                }else if(itemList.isNotEmpty()){
                    view.bindingAdapter.setChecked(0, true)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value =  ["scrollToPosition"], requireAll = false)
    fun scrollToPosition(view: RecyclerView,  scrollToPosition: Int = 0) {
        val adapter = view.bindingAdapter
        val itemList = adapter.models
        itemList?.apply {
            if (itemList.size > scrollToPosition){
                when(val layoutManager = view.layoutManager) {
                    is LinearLayoutManager -> {
                        layoutManager.scrollToPosition(scrollToPosition)
                    }
                }
            }else{
                return
            }
        }
    }

}
