package com.april.develop.ui

import android.app.Activity
import android.content.Intent
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/*

    让跳转和带值返回变得更方便

 */

fun FragmentActivity.startContractIntent(
    intent: Intent,
    result: OnContractResultListener? = null
) {
    startContractIntent(supportFragmentManager, intent, result)
}

fun FragmentActivity.startContractIntent(
    targetActivityClass: Class<out Activity>,
    result: OnContractResultListener? = null
) {
    startContractIntent(supportFragmentManager, Intent(this, targetActivityClass), result)
}

inline fun <reified A : Activity> FragmentActivity.startContractIntent(
    noinline result: OnContractResultListener? = null
) {
    startContractIntent(supportFragmentManager, Intent(this, A::class.java), result)
}

fun Fragment.startContractIntent(
    intent: Intent,
    result: OnContractResultListener? = null
) {
    startContractIntent(childFragmentManager, intent, result)
}

fun Fragment.startContractIntent(
    targetActivityClass: Class<out Activity>,
    result: OnContractResultListener? = null
) {
    startContractIntent(
        childFragmentManager,
        Intent(requireActivity(), targetActivityClass),
        result
    )
}

inline fun <reified A : Activity> Fragment.startContractIntent(
    noinline result: OnContractResultListener? = null
) {
    startContractIntent(childFragmentManager, Intent(requireActivity(), A::class.java), result)
}

fun startContractIntent(
    manager: FragmentManager,
    intent: Intent,
    result: OnContractResultListener? = null
) {
    ContractController.getInstance(manager).startContractIntent(intent, result)
}

/**
 * TODO 这个类暂时有点浪费，可以再添加一些常用功能
 */
class ContractController : Fragment() {
    companion object {
        private const val TAG = "ContractControllerTAG"

        internal fun getInstance(manager: FragmentManager): ContractController {
            var controller: ContractController? =
                manager.findFragmentByTag(TAG) as? ContractController
            if (controller == null) {
                controller = ContractController()
                manager.beginTransaction()
                    .add(controller, TAG)
                    .commitAllowingStateLoss()
                manager.executePendingTransactions()
            }
            return controller
        }
    }

    private var contractRequestCode: Int? = null
    private var contractResultListener: OnContractResultListener? = null

    fun startContractIntent(
        intent: Intent,
        resultListener: OnContractResultListener? = null
    ) {
        contractRequestCode = 0X101
        contractResultListener = resultListener
        startActivityForResult(intent, contractRequestCode!!)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == contractRequestCode) {
            contractResultListener?.invoke(resultCode, data)
        }
    }

}

typealias OnContractResultListener = (resultCode: Int, data: Intent?) -> Unit
