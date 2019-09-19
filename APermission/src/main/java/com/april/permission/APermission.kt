package com.april.permission

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


/*

        APermission.permissions(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )?.granted {
            toast("通过了权限")
        }?.denied {
            toast("拒绝了权限")
        }?.request()

 */

/**
 * 权限请求是依附与每一个 Activity 而言的，如果不存在任何一个 Activity 存活，则不能发起权限请求
 */
class APermission {

    companion object {

        const val TAG = "PermissionPromoterTAG"
        private val watcher = TopActivityWatcher()

        /**
         * （记得一定要先初始化，之后才能使用。由于 初始化 不是 耗时 操作，所以最好是放在 Application 里面进行。）
         *
         * [application] 初始化权限请求框架
         */
        fun init(application: Application) {
            application.registerActivityLifecycleCallbacks(watcher)
        }

        /**
         * 构建出 Promoter
         *
         * [permissions] 需要申请的权限
         */
        fun permissions(vararg permissions: String): Promoter? {
            val manager = watcher.getTopFragmentManager()
            if (manager != null) {
                var promoter = manager.findFragmentByTag(TAG) as? Promoter
                if (promoter == null) {
                    promoter = Promoter()
                    manager.beginTransaction()
                        .add(promoter, TAG)
                        .commitAllowingStateLoss()
                    manager.executePendingTransactions()
                }
                promoter.reset(Array(permissions.size) {
                    permissions[it]
                })
                return promoter
            }
            return null
        }

    }

}

/**
 * 权限请求最终发起者
 */
class Promoter : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    //权限请求码
    private var requestCode: Int = 0X0119
    //需要申请的权限
    private var requestPermissions = arrayOf<String>()
    //权限通过回调
    private var grantedCallBack: ((requestPermissions: Array<String>) -> Unit)? = null
    //权限拒绝回调
    private var deniedCallBack: ((deniedPermissions: Array<String>) -> Unit)? = null

    /**
     * 重设相关配置
     *
     * [permissions] 需要申请的权限
     */
    internal fun reset(permissions: Array<String>) {
        this.requestPermissions = permissions
        grantedCallBack = null
        deniedCallBack = null
    }

    /**
     * 权限通过时回调
     *
     * [MutableList] 申请的权限
     */
    fun granted(block: ((requestPermissions: Array<String>) -> Unit)? = null): Promoter {
        grantedCallBack = block
        return this
    }

    /**
     * 权限被拒绝时回调
     *
     * [MutableList] 被拒绝的权限
     */
    fun denied(block: ((deniedPermissions: Array<String>) -> Unit)? = null): Promoter {
        deniedCallBack = block
        return this
    }

    /**
     * 发起请求
     */
    fun request(requestCode: Int = this.requestCode) {
        this.requestCode = requestCode
        //6.0及以上才需要申请
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(requestPermissions, this.requestCode)
        }
        //6.0以下直接算作通过权限
        else {
            grantedCallBack?.invoke(requestPermissions)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != this.requestCode) {
            return
        }
        activity?.apply {
            //被拒绝的权限
            getDeniedPermissions(this, permissions).apply {
                if (this.isEmpty()) {
                    grantedCallBack?.invoke(requestPermissions)
                } else {
                    deniedCallBack?.invoke(this)
                }
            }
        }
    }

    /**
     * 获取被拒绝的权限
     *
     * [permissions] 需要检测的权限
     */
    private fun getDeniedPermissions(
        context: Context,
        permissions: Array<out String>
    ): Array<String> {
        val deniedPermissions = mutableListOf<String>().apply {
            for (permission in permissions) {
                if (PackageManager.PERMISSION_DENIED ==
                    context.checkPermission(permission, Process.myPid(), Process.myUid())
                ) {
                    add(permission)
                }
            }
        }
        return if (deniedPermissions.isEmpty()) {
            emptyArray()
        } else {
            Array(deniedPermissions.size) {
                deniedPermissions[it]
            }
        }
    }

}

object Permissions {

    //手机
    val PHONE = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.ADD_VOICEMAIL,
        Manifest.permission.USE_SIP,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        //8.0新权限
        //允许应用通过编程方式接听呼入电话。
        //要在应用中处理呼入电话，可以使用
        // acceptRingingCall() 函数。
        Manifest.permission.ANSWER_PHONE_CALLS,
        //允许应用读取设备中存储的电话号码。
        Manifest.permission.READ_PHONE_NUMBERS
    )

    //日历
    val CALENDAR = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    //相机
    val CAMERA = arrayOf(
        Manifest.permission.CAMERA
    )

    //通讯
    val CONTACTS = arrayOf(
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.GET_ACCOUNTS
    )

    //定位
    val LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //麦克风，扩音器，话筒
    val MICROPHONE = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    //传感器
    val SENSORS = arrayOf(
        Manifest.permission.BODY_SENSORS
    )

    //短信
    val SMS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_MMS
    )

    //存储
    val STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //安装权限
    val INSTALL = arrayOf(
        //8.0 新权限
        //安装未知应用，需要此权限
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    )
}

/**
 * 监听 Activity
 */
private class TopActivityWatcher : Application.ActivityLifecycleCallbacks {

    private val activityList: MutableList<Activity> = arrayListOf()

    /**
     * [FragmentManager] 获取最上面一个 Activity 的 FragmentManager
     */
    internal fun getTopFragmentManager(): FragmentManager? {
        if (activityList.isNotEmpty()) {
            val last = activityList.last()
            if (last is FragmentActivity) {
                return last.supportFragmentManager
            }
        }
        return null
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activityList.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activityList.add(activity!!)
    }

}