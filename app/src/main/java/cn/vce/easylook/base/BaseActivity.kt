package cn.vce.easylook.base


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.vce.easylook.R
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

open class BaseActivity : AppCompatActivity() {

    lateinit var receiver: ForceOfflineReceiver
    companion object{
        private const val tag : String="BaseActivity"
    }

    private val className: String = javaClass.simpleName
    //知道当前处于哪一个activity
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(tag, "$className-onCreate:activity的初始化操作，绑定布局、绑定事件等。")
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "$className-onStart:在activity由不可见变为可见的时候调用。")
    }

    override fun onResume() {
        super.onResume()
        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction("cn.vce.easylook.FORCE_OFFLINE")
        receiver = ForceOfflineReceiver()
        registerReceiver(receiver, intentFilter)

        Log.d(tag,"$className-onResume:将准备好并处于栈顶的activity和用户进行交互。")
    }

    override fun onPause() {
        super.onPause()
        //取消注册广播
        unregisterReceiver(receiver)
        Log.d(tag,"$className-onPause:在系统准备去启动或者恢复另一个activity的时候调用。")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag,"$className-onStop:activity完全不可见的时候调用。")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(tag,"$className-onRestart:在activity由停止状态变为运行状态之前调用。")
    }

    //没有执行onDestroy方法，那activity启动的时候还是调用onRestart方法
    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag,"$className-onDestroy:在activity被销毁之前调用，activity变为销毁状态。")
        ActivityCollector.removeActivity(this)
    }

    override fun onSaveInstanceState(outState : Bundle){
        super.onSaveInstanceState(outState)
    }

    inner class ForceOfflineReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null) {
                AlertDialog.Builder(context).apply{
                    setTitle(getString(R.string.warning))
                    setMessage(getString(R.string.exit_prompt))
                    setCancelable(false)
                    setPositiveButton(getString(R.string.ok)){ _,_ ->
                        ActivityCollector.finishAll()
                        //下面代码可用于重新登录，但本项目没有登录
                        //val i = Intent(p0, LoginActivity::class.java)
                        //p0.startActivity(i)
                    }
                    show()
                }
            }
        }

    }

}

fun main() {
    //1
    try {
        val cla = Class.forName("cn.vce.easylook.base.ActivityCollector")
        val constructor: Constructor<*> = cla.getDeclaredConstructor()
        constructor.isAccessible = true
        val obj = constructor.newInstance() as ActivityCollector
        println(obj)
    }catch (e: InvocationTargetException){
        e.printStackTrace()
    }
}