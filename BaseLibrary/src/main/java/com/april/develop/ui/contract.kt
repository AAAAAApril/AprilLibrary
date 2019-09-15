package com.april.develop.ui

import android.content.Intent
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlin.random.Random

fun FragmentActivity.startContractIntent(intent: Intent,
                                         result: (resultCode: Int, data: Intent?) -> Unit) {
    startContractIntent(supportFragmentManager, intent, result)
}

fun Fragment.startContractIntent(intent: Intent,
                                 result: (resultCode: Int, data: Intent?) -> Unit) {
    startContractIntent(childFragmentManager,intent, result)
}

fun startContractIntent(manager: FragmentManager,
                        intent: Intent,
                        result: (resultCode: Int, data: Intent?) -> Unit) {
    ContractController.getInstance(manager).startContractIntent(intent, result)
}

/**
 * TODO 这个类暂时有点浪费，可以再添加一些常用功能
 */
class ContractController : Fragment() {
    companion object {
        private const val TAG = "ContractControllerTAG"

        internal fun getInstance(manager: FragmentManager): ContractController {
            var controller: ContractController? = manager.findFragmentByTag(TAG) as? ContractController
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
    private var contractResultListener: ((resultCode: Int, data: Intent?) -> Unit)? = null

    fun startContractIntent(intent: Intent,
                            resultListener: (resultCode: Int, data: Intent?) -> Unit) {
        contractRequestCode = Random.Default.nextInt()
        contractResultListener = resultListener
        startActivityForResult(intent, contractRequestCode!!)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contractResultListener?.apply {
            if (requestCode == contractRequestCode) {
                invoke(requestCode, data)
            }
        }
    }

}