package cn.vce.common.base


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

open class BaseActivity : AppCompatActivity() {

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
        Log.d(tag,"$className-onResume:将准备好并处于栈顶的activity和用户进行交互。")
    }

    override fun onPause() {
        super.onPause()
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

    fun toastLongScan(info: String){
        runOnUiThread{
            Toast.makeText(this, info, Toast.LENGTH_LONG).show()
        }
    }


    inner class ForcaOffline : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p0 != null) {
                AlertDialog.Builder(p0).apply{
                    setTitle("Warning")
                    setMessage("You are force to be offline.Please try to login again.")
                    setCancelable(false)
                    setPositiveButton("OK"){ _,_ ->
                        ActivityCollector.finishAll()
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
        val cla = Class.forName("cn.vce.common.base.ActivityCollector")
        val constructor: Constructor<*> = cla.getDeclaredConstructor()
        constructor.isAccessible = true
        val obj = constructor.newInstance() as ActivityCollector
        println(obj)
    }catch (e: InvocationTargetException){
        e.printStackTrace()
    }
}