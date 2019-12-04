package com.april.develop.vvm

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 视图层 约束接口
 */
interface IViewContract {
    val toastObserver: IToastObserver
    val loadingObserver: ILoadingObserver
}

interface IToastObserver : Observer<CharSequence?> {
    override fun onChanged(t: CharSequence?)
}

interface ILoadingObserver : Observer<Boolean> {
    override fun onChanged(t: Boolean)
}

/**
 * Activity 内获取 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> FragmentActivity.obtainVM(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this).get(VM::class.java).apply {
        val contract = this@obtainVM as IViewContract
        contractToastLiveData.observe(this@obtainVM, contract.toastObserver)
        contractLoadingLiveData.observe(this@obtainVM, contract.loadingObserver)
    }
}

/**
 * Fragment 内获取 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.obtainVM(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this).get(VM::class.java).apply {
        val contract = this@obtainVM as IViewContract
        contractToastLiveData.observe(this@obtainVM, contract.toastObserver)
        contractLoadingLiveData.observe(this@obtainVM, contract.loadingObserver)
    }
}

/**
 * Fragment 获取 父 Fragment 的 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.obtainVMFromParent(): VM {
    assert((this.parentFragment as? IViewContract) != null) {
        "${this.javaClass.name} 的 ParentFragment 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this.requireParentFragment()).get(VM::class.java)
//        .apply {
//            val parentFragment = this@obtainVMFromParent.parentFragment ?: return@apply
//            val contract = parentFragment as IViewContract
//
//            contractToastLiveData.removeObserver(contract.toastObserver)
//            contractToastLiveData.observe(parentFragment, contract.toastObserver)
//
//            contractLoadingLiveData.removeObserver(contract.loadingObserver)
//            contractLoadingLiveData.observe(parentFragment, contract.loadingObserver)
//        }
}

/**
 * Fragment 获取 宿主 Activity 的 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.obtainVMFromHostActivity(): VM {
    assert((this.activity as? IViewContract) != null) {
        "${this.javaClass.name} 的宿主 Activity 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this.requireActivity()).get(VM::class.java)
//        .apply {
//            val activity = this@obtainVMFromHostActivity.activity ?: return@apply
//            val contract = activity as IViewContract
//
//            contractToastLiveData.removeObserver(contract.toastObserver)
//            contractToastLiveData.observe(activity, contract.toastObserver)
//
//            contractLoadingLiveData.removeObserver(contract.loadingObserver)
//            contractLoadingLiveData.observe(activity, contract.loadingObserver)
//        }
}

/**
 * ViewModel 抽象类
 */
abstract class ContractViewModel(application: Application) : AndroidViewModel(application) {

    val contractToastLiveData by lazy {
        MutableLiveData<CharSequence?>("")
    }

    val contractLoadingLiveData by lazy {
        MutableLiveData<Boolean>(false)
    }

    protected fun onShowToast(message: CharSequence?) {
        contractToastLiveData.postValue(message)
    }

    protected fun onShowLoading(show: Boolean) {
        contractLoadingLiveData.postValue(show)
    }

    protected open suspend fun tryLaunch(
        onBeforeTry: () -> Boolean = { true },
        onTryLaunch: suspend () -> Unit = {},
        onException: () -> Unit = {},
        onFinally: () -> Boolean = { true }
    ): Job {
        return viewModelScope.launch {
            if (onBeforeTry.invoke()) {
                onShowLoading(true)
            }
            try {
                onTryLaunch.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                onException.invoke()
            } finally {
                if (onFinally.invoke()) {
                    onShowLoading(false)
                }
            }
        }
    }

    protected open suspend fun tryLaunchDSL(block: ViewModelTryLaunch.() -> Unit): Job {
        return ViewModelTryLaunch().apply(block).let {
            viewModelScope.launch {
                if (it.onBeforeTry.invoke()) {
                    onShowLoading(true)
                }
                try {
                    it.onTryLaunch.invoke()
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.onException.invoke()
                } finally {
                    if (it.onFinally.invoke()) {
                        onShowLoading(false)
                    }
                }
            }
        }
    }

}

class ViewModelTryLaunch {
    //是否执行默认的行为
    var onBeforeTry: () -> Boolean = { true }
    var onTryLaunch: suspend () -> Unit = {}
    //是否执行默认的行为
    var onException: () -> Unit = {}
    //是否执行默认的行为
    var onFinally: () -> Boolean = { true }
}