package com.april.develop.manager

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import kotlin.system.exitProcess

/**
 * Activity 栈管理
 */
object ActivityManager : Application.ActivityLifecycleCallbacks {

    /**
     * 初始化
     */
    @JvmStatic
    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 关闭其他所有 Activity
     */
    @JvmStatic
    fun <A : Activity> finishOthersAll(myself: A) {
        activitySet.forEach {
            if (it != myself) {
                it.finish()
            }
        }
    }

    /**
     * 关闭所有，除了指定的 Activity
     */
    @JvmStatic
    fun <A : Activity> finishAllWithout(keepClass: Class<A>) {
        activitySet.forEach {
            if (it.javaClass != keepClass) {
                it.finish()
            }
        }
    }

    /**
     * 关闭某个 Activity
     */
    @JvmStatic
    fun <A : Activity> finishSomeone(someoneActivityClass: Class<A>) {
        activitySet.findLast {
            return@findLast it.javaClass == someoneActivityClass
        }?.finish()
    }

    /**
     * 关闭所有，然后跳转
     */
    @JvmStatic
    fun finishAllAndStart(intent: Intent) {
        activitySet.forEach {
            if (it == activitySet.last()) {
                it.startActivity(intent)
            }
            it.finish()
        }
    }

    /**
     * 关闭所有，然后退出
     */
    @JvmStatic
    fun finishAllAndExit() {
        activitySet.forEach {
            it.finish()
        }
        exitProcess(0)
    }

    private val activitySet = mutableSetOf<Activity>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activitySet.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activitySet.remove(activity)
    }


}