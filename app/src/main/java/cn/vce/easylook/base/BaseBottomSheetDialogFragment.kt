package cn.vce.easylook.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 *
 */
abstract class BaseBottomSheetDialogFragment<BD : ViewDataBinding> : BottomSheetDialogFragment() {
    /**
     * 开放给外部使用
     */
    lateinit var mContext: Context
    lateinit var mActivity: AppCompatActivity
    private var fragmentProvider: ViewModelProvider? = null
    private var activityProvider: ViewModelProvider? = null
    protected lateinit var binding: BD
    private var mBinding: ViewDataBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //由于同一个fragment对象可能被activity attach多次(比如viewPager+PagerStateAdapter中)
        //所以fragmentViewModel不能放在onCreateView初始化，否则会产生多个fragmentViewModel
        initActivityViewModel()
        initFragmentViewModel()
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as AppCompatActivity
        // 必须要在Activity与Fragment绑定后，因为如果Fragment可能获取的是Activity中ViewModel
        // 必须在onCreateView之前初始化viewModel，因为onCreateView中需要通过ViewModel与DataBinding绑定
        initViewModel()
        ParamUtil.initParam(this)
    }*/

    override fun onStart() {
        super.onStart()
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //mRootView = inflater.inflate(getLayoutResId(), container, false)
        getLayoutId()?.let {
            binding = DataBindingUtil.inflate(inflater, it, container, false)
            //将ViewDataBinding生命周期与Fragment绑定
            binding.lifecycleOwner = viewLifecycleOwner

            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return binding.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(savedInstanceState)
        //observe一定要在初始化最后，因为observe会收到黏性事件，随后对ui做处理
        observe()
        onClick()
    }

    /**
     * 初始化viewModel
     * 之所以没有设计为抽象，是因为部分简单activity可能不需要viewModel
     * observe同理
     */
    open fun initViewModel() {

    }

    open fun initActivityViewModel() {

    }

    open fun initFragmentViewModel() {

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
        if (activityProvider == null) {
            activityProvider = ViewModelProvider(mActivity)
        }
        return activityProvider!![modelClass]
    }


    protected inline fun <reified T : ViewModel> getFragmentViewModel(): T = getFragmentViewModel(T::class.java)


    /**
     * 通过fragment获取viewModel，跟随fragment生命周期
     */
    protected open fun <T : ViewModel> getFragmentViewModel(modelClass: Class<T>): T {
        if (fragmentProvider == null) {
            fragmentProvider = ViewModelProvider(this)
        }
        return fragmentProvider!![modelClass]
    }

    /**
     * fragment跳转
     */
    protected fun nav(): NavController {
        return NavHostFragment.findNavController(this)
    }

    /**
     * 点击事件
     */
    open fun onClick() {

    }

    /**
     * 初始化View以及事件
     */
    open fun initView() {

    }

    /**
     * 加载数据
     */
    open fun loadData() {

    }

        /**
     * 初始化入口
     */
    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 获取layout布局
     */
    abstract fun getLayoutId(): Int?

    fun showIt(context: AppCompatActivity) {
        val fm = context.supportFragmentManager
        show(fm, "dialog")
    }


    fun show(context: Context) {
        mActivity = context as AppCompatActivity
        mContext = context
        val ft = mActivity.supportFragmentManager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    fun hide() {
        val ft = mActivity.supportFragmentManager.beginTransaction()
        ft.remove(this)
        ft.commitAllowingStateLoss()
    }
}