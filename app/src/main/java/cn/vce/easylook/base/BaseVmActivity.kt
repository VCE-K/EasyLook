package cn.vce.easylook.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * des mvvm 基础 activity
 * @date 2020/5/9
 * @author zs
 */
abstract class BaseVmActivity<BD : ViewDataBinding> : AppCompatActivity() {

    private var mActivityProvider: ViewModelProvider? = null

    protected lateinit var binding: BD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getLayoutId()!!.let { DataBindingUtil.setContentView(this, it) }
        //setContentView(binding.root)
        /*setStatusColor()
        setSystemInvadeBlack()*/
        initViewModel()
        observe()
        init(savedInstanceState)
    }



    /**
     * 设置状态栏背景颜色
     */
    /*open fun setStatusColor() {
        StatusUtils.setUseStatusBarColor(this, ColorUtils.parseColor("#00ffffff"))
    }*/

    /**
     * 沉浸式状态
     */
    /*open fun setSystemInvadeBlack() {
        //第二个参数是是否沉浸,第三个参数是状态栏字体是否为黑色。
        StatusUtils.setSystemStatus(this, true, true)
    }*/

    /**
     * 初始化viewModel
     * 之所以没有设计为抽象，是因为部分简单activity可能不需要viewModel
     * observe同理
     */
    open fun initViewModel() {
    }

    /**
     * 注册观察者
     */
    open fun observe() {

    }

    protected inline fun <reified T : ViewModel> getActivityViewModel(): T = getActivityViewModel(T::class.java)


    /**
     * 通过activity获取viewModel，跟随activity生命周期
     */
    protected fun <T : ViewModel> getActivityViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this)
        }
        return mActivityProvider!![modelClass]
    }

    /**
     * 初始化View以及事件
     */
    open fun initView() {

    }

    /**
     * activity入口
     */
    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 获取layout布局
     */
    abstract fun getLayoutId(): Int?
}