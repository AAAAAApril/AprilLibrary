package com.april.develop.vvm

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

/**
 * 视图层 约束接口
 */
interface IViewContract : Observer<ContractBean> {

    fun onShowToast(message: CharSequence?)

    fun onShowLoading(show: Boolean?)

    override fun onChanged(t: ContractBean?) {
        when (t?.contractType) {
            ContractType.ShowToast -> {
                onShowToast(t.message)
            }
            ContractType.ShowLoading -> {
                onShowLoading(t.showLoading)
            }
        }
    }

}

/**
 * Activity 内获取 ContractViewModel
 */
inline fun <reified VM : ContractViewModel<*>> FragmentActivity.obtainVM(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this).get(VM::class.java).apply {
        contractLiveData.observe(this@obtainVM, this@obtainVM as IViewContract)
    }
}

/**
 * Fragment 内获取 ContractViewModel
 */
inline fun <reified VM : ContractViewModel<*>> Fragment.obtainVM(): VM {
    assert((this as? IViewContract) != null) {
        "${this.javaClass.name} 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this).get(VM::class.java).apply {
        contractLiveData.observe(this@obtainVM, this@obtainVM as IViewContract)
    }
}

/**
 * Fragment 获取 父 Fragment 的 ContractViewModel
 */
inline fun <reified VM : ContractViewModel<*>> Fragment.obtainVMFromParent(): VM {
    assert((this.parentFragment as? IViewContract) != null) {
        "${this.javaClass.name} 的 ParentFragment 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this.requireParentFragment()).get(VM::class.java)
}

/**
 * Fragment 获取 宿主 Activity 的 ContractViewModel
 */
inline fun <reified VM : ContractViewModel<*>> Fragment.obtainVMFromHostActivity(): VM {
    assert((this.activity as? IViewContract) != null) {
        "${this.javaClass.name} 的宿主 Activity 不能为 null，且必须实现 IViewContract 接口"
    }
    return ViewModelProviders.of(this.requireActivity()).get(VM::class.java)
}

/**
 * ViewModel 抽象类
 */
abstract class ContractViewModel<AbstractBean>(application: Application) :
    AndroidViewModel(application) {

    val contractLiveData by lazy {
        MutableLiveData<ContractBean>()
    }

    protected abstract fun <T> onContract(bean: AbstractBean?): T?

    protected fun onShowToast(message: CharSequence?) {
        contractLiveData.postValue(
            ContractBean(
                ContractType.ShowToast,
                message = message
            )
        )
    }

    protected fun onShowLoading(show: Boolean) {
        contractLiveData.postValue(
            ContractBean(
                ContractType.ShowLoading,
                showLoading = show
            )
        )
    }

}

/**
 * 数据实体抽象类
 */
data class ContractBean(
    val contractType: ContractType,
    val showLoading: Boolean? = null,
    val message: CharSequence? = null
)

/**
 * 约束行为类型
 */
enum class ContractType {
    //消息提示
    ShowToast,
    //加载提示
    ShowLoading
}