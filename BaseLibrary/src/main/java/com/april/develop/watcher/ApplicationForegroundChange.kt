package com.april.develop.watcher

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * 监听
 * 应用前后台状态变化的工具类
 * 仅限于使用 Activity 作为应用主体的项目
 * 如果项目本身没有任何的界面元素，则无法监听
 */
class ApplicationForegroundWatcher : Application.ActivityLifecycleCallbacks {

    companion object {

        /**
         * 构造
         */
        fun watch(application: Application, listener: OnApplicationForegroundChangeListener? = null) {
            application.registerActivityLifecycleCallbacks(ApplicationForegroundWatcher().apply {
                this.foregroundChangeListener = listener
            })
        }

        private val listenerSet = mutableSetOf<OnApplicationForegroundChangeListener?>()

        /**
         * 监听
         */
        fun listen(listener: OnApplicationForegroundChangeListener) {
            listenerSet.add(listener)
        }

        /**
         * 取消监听
         */
        fun ignore(listener: OnApplicationForegroundChangeListener) {
            listenerSet.remove(listener)
        }

    }

    private var foregroundChangeListener: OnApplicationForegroundChangeListener? = null

    private var topForegroundActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
        (activity as? OnApplicationForegroundChangeListener)?.apply {
            listen(this)
        }
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        if (topForegroundActivity == null) {
            foregroundChangeListener?.onApplicationForegroundChanged(true)
            listenerSet.map {
                it?.onApplicationForegroundChanged(true)
            }
        }
        topForegroundActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        if (topForegroundActivity == activity) {
            foregroundChangeListener?.onApplicationForegroundChanged(false)
            listenerSet.map {
                it?.onApplicationForegroundChanged(false)
            }
            topForegroundActivity = null
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        (activity as? OnApplicationForegroundChangeListener)?.apply {
            ignore(this)
        }
    }

}

interface OnApplicationForegroundChangeListener {

    /**
     * @param foreground 应用是否在前台
     */
    fun onApplicationForegroundChanged(foreground: Boolean)
}