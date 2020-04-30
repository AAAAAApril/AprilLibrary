package com.april.develop.vvm

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * 由 页面组件 实现
 */
interface IContractView {
    /**
     * 显示 提示文字
     */
    fun onShowToast(message: CharSequence?)

    /**
     * 是否显示加载提示，和提示文字
     */
    fun onShowLoading(state: LoadingState)
}

/**
 * 由 ViewModel 实现
 */
interface IContractViewModel {
    /**
     * 提示 观察者
     */
    val toastLiveData: SingleLiveData<CharSequence?>

    /**
     * 加载状态 观察者
     */
    val loadingLiveData: SingleLiveData<LoadingState>
}

/**
 * 加载状态
 */
sealed class LoadingState(val tips: CharSequence?) {
    class LoadingStart(tips: CharSequence? = null) : LoadingState(tips)
    class LoadingEnd(tips: CharSequence? = null) : LoadingState(tips)
}

//==================================================================================================

/**
 * 快捷启动任务
 */
fun <VM> VM.launch(
    onBefore: () -> Unit = {
        loadingLiveData.postValue(LoadingState.LoadingStart())
    },
    onException: (Throwable) -> Unit = {},
    onFinally: () -> Unit = {
        loadingLiveData.postValue(LoadingState.LoadingEnd())
    },
    block: suspend CoroutineScope.() -> Unit
): Job where VM : ViewModel,
             VM : IContractViewModel {
    return viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onException.invoke(throwable)
        }
    ) {
        onBefore.invoke()
        try {
            block.invoke(this)
        } finally {
            onFinally.invoke()
        }
    }
}

//==================================================================================================

/**
 * 获取当前生命周期绑定的 ViewModel
 */
@MainThread
inline fun <reified VM, F> F.contractViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {}
): Lazy<VM> where F : Fragment,
                  F : IContractView,
                  VM : ViewModel,
                  VM : IContractViewModel {
    return createContractViewModelLazy(
        VM::class,
        { viewModelStore },
        {
            if (whenCreated != null) {
                if (!it.toastLiveData.hasObservers()) {
                    it.toastLiveData.observe(this, this::onShowToast)
                }
                if (!it.loadingLiveData.hasObservers()) {
                    it.loadingLiveData.observe(this, this::onShowLoading)
                }
                whenCreated.invoke(it)
            }
        }
    )
}

/**
 * 获取 ParentFragment 生命周期绑定的 ViewModel
 */
@MainThread
inline fun <reified VM, F> F.contractParentViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {}
): Lazy<VM> where F : Fragment,
                  VM : ViewModel,
                  VM : IContractViewModel {
    return createContractViewModelLazy(
        VM::class,
        { requireParentFragment().viewModelStore },
        {
            if (whenCreated != null) {
                val parent = requireParentFragment()
                if (parent is IContractView) {
                    if (!it.toastLiveData.hasObservers()) {
                        it.toastLiveData.observe(parent, parent::onShowToast)
                    }
                    if (!it.loadingLiveData.hasObservers()) {
                        it.loadingLiveData.observe(parent, parent::onShowLoading)
                    }
                }
                whenCreated.invoke(it)
            }
        }
    )
}

/**
 * 获取 Activity 生命周期绑定的 ViewModel
 */
@MainThread
inline fun <reified VM, F> F.contractActivityViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {}
): Lazy<VM> where F : Fragment,
                  VM : ViewModel,
                  VM : IContractViewModel {
    return createContractViewModelLazy(
        VM::class,
        { requireActivity().viewModelStore },
        {
            if (whenCreated != null) {
                val activity = requireActivity()
                if (activity is IContractView) {
                    if (!it.toastLiveData.hasObservers()) {
                        it.toastLiveData.observe(activity, activity::onShowToast)
                    }
                    if (!it.loadingLiveData.hasObservers()) {
                        it.loadingLiveData.observe(activity, activity::onShowLoading)
                    }
                }
                whenCreated.invoke(it)
            }
        }
    )
}

/**
 * 获取当前生命周期绑定的 ViewModel
 */
@MainThread
inline fun <reified VM, A> A.contractViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {}
): Lazy<VM> where A : ComponentActivity,
                  A : IContractView,
                  VM : ViewModel,
                  VM : IContractViewModel {
    return ContractViewModelLazy(
        VM::class,
        { viewModelStore },
        { defaultViewModelProviderFactory },
        {
            if (whenCreated != null) {
                if (!it.toastLiveData.hasObservers()) {
                    it.toastLiveData.observe(this, this::onShowToast)
                }
                if (!it.loadingLiveData.hasObservers()) {
                    it.loadingLiveData.observe(this, this::onShowLoading)
                }
                whenCreated.invoke(it)
            }
        }
    )
}

//==================================================================================================

@MainThread
fun <VM : ViewModel> Fragment.createContractViewModelLazy(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    whenCreated: ((VM) -> Unit)?
): Lazy<VM> = ContractViewModelLazy(
    viewModelClass,
    storeProducer,
    { defaultViewModelProviderFactory },
    whenCreated
)

class ContractViewModelLazy<VM : ViewModel>(
    private val viewModelClass: KClass<VM>,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory,
    private val whenCreated: ((VM) -> Unit)?
) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory).get(viewModelClass.java).also {
                    whenCreated?.invoke(it)
                    cached = it
                }
            } else {
                viewModel
            }
        }

    override fun isInitialized() = cached != null
}

//==================================================================================================

/**
 * IContractViewModel 的一个代理实现
 */
class IContractViewModelImpl : IContractViewModel {
    override val toastLiveData by lazy { SingleLiveData<CharSequence?>() }
    override val loadingLiveData by lazy { SingleLiveData<LoadingState>() }
}
