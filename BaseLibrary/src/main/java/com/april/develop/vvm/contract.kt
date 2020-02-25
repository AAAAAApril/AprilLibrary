package com.april.base.vvm

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import kotlinx.coroutines.*

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
inline fun <reified VM : ContractViewModel> FragmentActivity.viewModel(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this).get(VM::class.java).apply {
        val contract = this@viewModel as IViewContract
        contractToastLiveData.observe(this@viewModel, contract.toastObserver)
        contractLoadingLiveData.observe(this@viewModel, contract.loadingObserver)
    }
}

/**
 * Fragment 内获取 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.viewModel(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this).get(VM::class.java).apply {
        val contract = this@viewModel as IViewContract
        contractToastLiveData.observe(this@viewModel, contract.toastObserver)
        contractLoadingLiveData.observe(this@viewModel, contract.loadingObserver)
    }
}

/**
 * Fragment 获取 父 Fragment 的 ContractViewModel
 */
inline fun <reified VM : ContractViewModel> Fragment.viewModelFromParent(): VM {
    assert((this.parentFragment as? IViewContract) != null) {
        "${this.javaClass.name} 的 ParentFragment 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this.requireParentFragment()).get(VM::class.java).apply {
        val parentFragment = this@viewModelFromParent.parentFragment ?: return@apply
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
inline fun <reified VM : ContractViewModel> Fragment.viewModelFromHostActivity(): VM {
    assert((this.activity as? IViewContract) != null) {
        "${this.javaClass.name} 的宿主 Activity 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProvider(this.requireActivity()).get(VM::class.java).apply {
        val activity = this@viewModelFromHostActivity.activity ?: return@apply
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

    protected open fun launch(
        onBeforeTry: () -> Unit = { onShowLoading(true) },
        onException: (Throwable) -> Unit = {},
        onFinally: () -> Unit = { onShowLoading(false) },
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onException.invoke(throwable)
        }) {
            onBeforeTry.invoke()
            try {
                block.invoke(this)
            } finally {
                onFinally.invoke()
            }
        }
    }

    protected open fun launchDSL(block: ViewModelTryLaunch.() -> Unit): Job {
        return ViewModelTryLaunch().apply(block).let {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                it.onException.invoke(throwable)
            }) {
                if (it.onBeforeTry == null) {
                    onShowLoading(true)
                }
                try {
                    it.block.invoke(this)
                } finally {
                    if (it.onFinally == null) {
                        onShowLoading(false)
                    }
                }
            }
        }
    }

}

class ViewModelTryLaunch {
    var onBeforeTry: (() -> Unit)? = null
    var onException: (Throwable) -> Unit = {}
    var onFinally: (() -> Unit)? = null
    var block: suspend CoroutineScope.() -> Unit = {}
}

fun ViewModel.countDown(
    maxCount: Int = 60,
    block: suspend (Int) -> Unit
): Job {
    return viewModelScope.launch {
        repeat(maxCount) {
            block.invoke(maxCount - (it + 1))
            delay(1000)
        }
    }
}