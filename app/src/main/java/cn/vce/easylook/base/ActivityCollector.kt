package cn.vce.easylook.base

import android.app.Activity
import java.util.*

//object在kotlin表示单例
object ActivityCollector {
    private val activities = ArrayList<Activity>()

    fun addActivity(activity : Activity){
        activities.add(activity)
    }

    fun removeActivity(activity : Activity){
        activities.remove(activity)
    }

    //可以随时退出app
    fun finishAll(){
        for (activity in activities){
            if (!activity.isFinishing){
                activity.finish()
            }
        }
        activities.clear()
    }

}