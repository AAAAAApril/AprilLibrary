package com.april.develop.vvm

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
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

interface ILoadingObserver : Observer<Boolean?> {
    override fun onChanged(t: Boolean?)
}

/**
 * Activity 内获取 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> FragmentActivity.obtainViewModel(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this).get(VM::class.java).apply {
        val contract = this@obtainViewModel as IViewContract
        contractToastLiveData.observe(this@obtainViewModel, contract.toastObserver)
        contractLoadingLiveData.observe(this@obtainViewModel, contract.loadingObserver)
    }
}

/**
 * Fragment 内获取 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.obtainViewModel(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this).get(VM::class.java).apply {
        val contract = this@obtainViewModel as IViewContract
        contractToastLiveData.observe(this@obtainViewModel, contract.toastObserver)
        contractLoadingLiveData.observe(this@obtainViewModel, contract.loadingObserver)
    }
}

/**
 * Fragment 获取 父 Fragment 的 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.obtainViewModelFromParent(): VM {
    assert((this.parentFragment as? IViewContract) != null) {
        "${this.javaClass.name} 的 ParentFragment 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this.requireParentFragment()).get(VM::class.java).apply {
        val parentFragment = this@obtainViewModelFromParent.parentFragment ?: return@apply
        val contract = parentFragment as IViewContract
        if (!contractToastLiveData.hasObservers()) {
            contractToastLiveData.observe(parentFragment, contract.toastObserver)
        }
        if (!contractLoadingLiveData.hasObservers()) {
            contractLoadingLiveData.observe(parentFragment, contract.loadingObserver)
        }
    }
}

/**
 * Fragment 获取 宿主 Activity 的 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.obtainViewModelFromHostActivity(): VM {
    assert((this.activity as? IViewContract) != null) {
        "${this.javaClass.name} 的宿主 Activity 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this.requireActivity()).get(VM::class.java).apply {
        val activity = this@obtainViewModelFromHostActivity.activity ?: return@apply
        val contract = activity as IViewContract
        if (!contractToastLiveData.hasObservers()) {
            contractToastLiveData.observe(activity, contract.toastObserver)
        }
        if (!contractLoadingLiveData.hasObservers()) {
            contractLoadingLiveData.observe(activity, contract.loadingObserver)
        }
    }
}

/**
 * ViewModel 抽象类
 */
abstract class ContractViewModel(application: Application) : AndroidViewModel(application) {

    val contractToastLiveData by lazy {
        MutableLiveData<CharSequence?>()
    }

    val contractLoadingLiveData by lazy {
        MutableLiveData<Boolean?>()
    }

    protected fun onShowToast(message: CharSequence?) {
        contractToastLiveData.postValue(message)
    }

    protected fun onShowLoading(show: Boolean) {
        contractLoadingLiveData.postValue(show)
    }

    protected open suspend fun tryLaunch(
        onBeforeTry: (() -> Boolean)? = { true },
        onTryLaunch: suspend (CoroutineScope) -> Unit = {},
        onException: ((Throwable) -> Unit)? = null,
        onFinally: (() -> Boolean)? = { true }
    ): Job {
        return viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onException?.invoke(throwable)
        }) {
            if (onBeforeTry?.invoke() == true) {
                onShowLoading(true)
            }
            try {
                onTryLaunch.invoke(this)
            } finally {
                if (onFinally?.invoke() == true) {
                    onShowLoading(false)
                }
            }
        }
    }

    protected open suspend fun tryLaunchDSL(block: ViewModelTryLaunch.() -> Unit): Job {
        return ViewModelTryLaunch().apply(block).let {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                it.onException?.invoke(throwable)
            }) {
                if (it.onBeforeTry?.invoke() == true) {
                    onShowLoading(true)
                }
                try {
                    it.onTryLaunch.invoke(this)
                } finally {
                    if (it.onFinally?.invoke() == true) {
                        onShowLoading(false)
                    }
                }
            }
        }
    }

}

class ViewModelTryLaunch {
    //是否执行默认的行为
    var onBeforeTry: (() -> Boolean)? = { true }
    var onTryLaunch: suspend (CoroutineScope) -> Unit = {}
    var onException: ((Throwable) -> Unit)? = null
    //是否执行默认的行为
    var onFinally: (() -> Boolean)? = { true }
}