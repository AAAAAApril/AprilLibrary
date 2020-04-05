package com.april.develop.helper

import android.app.Activity
import android.app.Application
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay

/**
 * 全局变成灰色
 */
object GrayWindowHelper : Application.ActivityLifecycleCallbacks {

    private val configurationMap = mutableMapOf<Activity, GrayConfiguration>()

    /**
     * 切换，在灰色和原色之间切换
     */
    fun switch(activity: Activity) {
        configurationMap[activity]?.let {
            val targetConfiguration = if (it.default) {
                it.getGrayConfiguration()
            } else {
                it.getDefaultConfiguration()
            }
            activity.window.decorView.setLayerType(
                targetConfiguration.layerType,
                targetConfiguration.layerPaint
            )
        }
    }

    /**
     * 切换到原色
     */
    fun switchToDefault(activity: Activity) {
        configurationMap[activity]?.getDefaultConfiguration()?.let {
            activity.window.decorView.setLayerType(
                it.layerType,
                it.layerPaint
            )
        }
    }

    /**
     * 切换到灰色
     */
    fun switchToGray(activity: Activity) {
        configurationMap[activity]?.getGrayConfiguration()?.let {
            activity.window.decorView.setLayerType(
                it.layerType,
                it.layerPaint
            )
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.window.decorView.apply {
            configurationMap[activity] = GrayConfiguration(
                ConfigurationBean(
                    this.layerType,
                    null
                )
            )
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is ComponentActivity) {
            activity.lifecycleScope.launchWhenResumed {
                while (true) {
                    /*
                       测试功能：隔 1 秒切换一次
                        FIXME 这里有个 BUG，触发了 onPaused 之后再回来，
                         单数次返回时，没有执行切换，双数次则能正常执行……
                     */
                    delay(1 * 1000)
                    switch(activity)
                }
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}

private class GrayConfiguration(
    //默认设置
    private val defaultConfigurationBean: ConfigurationBean,
    //当前是否是默认配置
    var default: Boolean = true
) {
    private val grayConfigurationBean by lazy {
        ConfigurationBean(
            View.LAYER_TYPE_HARDWARE,
            Paint().apply {
                colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                    setSaturation(0f)
                })
            }
        )
    }

    /**
     * 获取默认的设置
     */
    fun getDefaultConfiguration(): ConfigurationBean {
        default = true
        return defaultConfigurationBean
    }

    /**
     * 获取灰色配置
     */
    fun getGrayConfiguration(): ConfigurationBean {
        default = false
        return grayConfigurationBean
    }

}

private data class ConfigurationBean(
    val layerType: Int,
    val layerPaint: Paint?
)

