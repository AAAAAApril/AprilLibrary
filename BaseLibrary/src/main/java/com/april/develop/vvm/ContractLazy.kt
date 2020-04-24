package com.april.develop.vvm

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
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
    fun onShowLoading(show: Boolean, tips: CharSequence?)
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
    class LoadingStart(tips: CharSequence?) : LoadingState(tips)
    class LoadingEnd(tips: CharSequence?) : LoadingState(tips)
}

//==================================================================================================

@MainThread
inline fun <reified VM, F> F.contractViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {

    }
): Lazy<VM>
        where F : Fragment,
              F : IContractView,
              VM : ViewModel,
              VM : IContractViewModel {
    return createContractViewModelLazy(VM::class, { viewModelStore }, whenCreated)
}

@MainThread
inline fun <reified VM, F> F.contractParentViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {

    }
): Lazy<VM>
        where F : Fragment,
              F : IContractView,
              VM : ViewModel,
              VM : IContractViewModel {
    return createContractViewModelLazy(
        VM::class,
        { requireParentFragment().viewModelStore },
        whenCreated
    )
}

@MainThread
inline fun <reified VM, F> F.contractActivityViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {

    }
): Lazy<VM>
        where F : Fragment,
              F : IContractView,
              VM : ViewModel,
              VM : IContractViewModel {
    return createContractViewModelLazy(
        VM::class,
        { requireActivity().viewModelStore },
        whenCreated
    )
}

@MainThread
inline fun <reified VM, A> A.contractViewModels(
    noinline whenCreated: ((VM) -> Unit)? = {

    }
): Lazy<VM>
        where A : ComponentActivity,
              A : IContractView,
              VM : ViewModel,
              VM : IContractViewModel {
    return ContractViewModelLazy(
        VM::class,
        { viewModelStore },
        { defaultViewModelProviderFactory },
        whenCreated
    )
}

//==================================================================================================

@MainThread
fun <VM : ViewModel> Fragment.createContractViewModelLazy(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    whenCreated: ((VM) -> Unit)?
): Lazy<VM> {
    return ContractViewModelLazy(
        viewModelClass,
        storeProducer,
        { defaultViewModelProviderFactory },
        whenCreated
    )
}

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

