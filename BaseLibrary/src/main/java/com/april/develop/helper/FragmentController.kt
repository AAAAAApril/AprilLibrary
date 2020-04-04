package com.april.develop.helper

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 控制 Fragment 的显示与隐藏，并协助处理视图恢复
 */
class FragmentController(
    //show hide 模式，或者 replace 模式
    private val replaceMode: Boolean = false
) {
    //用于在状态暂存时，存储当前正在显示的 Fragment 的 index
    private val savedStateKey: String = "${FragmentController::class.java.name}_SavedStateKey"

    private lateinit var manager: FragmentManager

    //装载 Fragment 的容器 View id
    @IdRes
    private var containerViewId: Int = View.NO_ID

    //正在显示的位置坐标
    private var showingIndex: Int = 0

    //正在显示的 Fragment 的 Class
    private var showingFragmentClass: Class<out Fragment>? = null

    //fragment 构建器
    private lateinit var creator: FragmentCreator

    /**
     * ①
     * 关联到 FragmentManger
     */
    @JvmOverloads
    fun onCreate(
        manager: FragmentManager,
        savedInstanceState: Bundle?,
        defaultSelectIndex: Int = this.showingIndex
    ): FragmentCreator {
        this.manager = manager
        showingIndex = savedInstanceState?.getInt(savedStateKey) ?: defaultSelectIndex
        creator = FragmentCreator(manager)
        return creator
    }

    /**
     * ②
     * 设置 Fragment 显示的位置
     *
     * 视图恢复时，会默认显示曾经显示的 Fragment，不需要再手动显示
     *
     * @return 正在选中的位置
     */
    fun onViewCreated(@IdRes containerViewId: Int): Int {
        this.containerViewId = containerViewId
        showFragment(showingIndex)
        return showingIndex
    }

    /**
     * ③
     * 显示一个 Fragment
     */
    fun <F : Fragment> showFragment(fragmentClass: Class<F>) {
        this.showingIndex = creator.getIndexByFragmentClass(fragmentClass)
        if (replaceMode) {
            replace(fragmentClass)
        } else {
            showAndHide(fragmentClass)
        }
    }

    /**
     * ③
     * 显示该位置上对应的 Fragment
     */
    fun showFragment(index: Int) {
        showFragment(creator.getFragmentClassByIndex(index))
    }

    /**
     * ④
     * 处理状态回收
     */
    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(savedStateKey, showingIndex)
    }

    /**
     * Fragment 的数量
     */
    fun getFragmentCount(): Int = creator.fragmentSize()

    /**
     * replace 模式
     */
    private fun replace(fragmentClass: Class<out Fragment>) {
        //要显示的 Fragment 正在显示
        if (fragmentClass == showingFragmentClass) {
            return
        }
        val target = creator.obtainFragment(fragmentClass)
        if (target != null) {
            manager.beginTransaction()
                .replace(containerViewId, target, target.asTag())
                .commit()
        } else {
            manager.beginTransaction()
                .replace(containerViewId, fragmentClass, Bundle(), fragmentClass.asTag())
                .commit()
        }
        showingFragmentClass = fragmentClass
    }

    /**
     * show hide 模式
     */
    private fun showAndHide(fragmentClass: Class<out Fragment>) {
        //要显示的 Fragment 正在显示
        if (fragmentClass == showingFragmentClass) {
            //正在显示的 Fragment
            val showingFragment = creator.obtainFragment(fragmentClass)
            if (showingFragment != null) {
                //如果正在显示的被隐藏了，则显示它
                if (showingFragment.isHidden) {
                    manager.beginTransaction()
                        .show(showingFragment)
                        .commit()
                }
            }
//            else {
            // 这里似乎不需要做处理，因为正在显示的 Fragment 不可能查出来为 null
//            }
            return
        }
        //要显示的 Fragment 没有显示
        //要显示的
        val targetFragment = creator.obtainFragment(fragmentClass)
        //正在显示的
        val showingFragment: Fragment? = if (showingFragmentClass != null) {
            creator.obtainFragment(showingFragmentClass!!)
        } else {
            null
        }
        //要显示的 Fragment 有实例
        if (targetFragment != null) {
            hideAndShow(showingFragment, targetFragment)
        }
        //要显示的 Fragment 没有实例
        else {
            hideAndShow(showingFragment, fragmentClass)
        }
        showingFragmentClass = fragmentClass
    }

    /**
     * 当要显示的 Fragment 有实例时
     */
    private fun hideAndShow(hideFragment: Fragment?, showFragment: Fragment) {
        manager.beginTransaction().apply {
            //要显示的 Fragment 是添加过的，此时正在显示的 Fragment 一定不为 null
            if (showFragment.isAdded) {
                //如果正在显示的 Fragment 可见
                if (hideFragment != null) {
                    if (hideFragment.isVisible) {
                        hide(hideFragment)
                    }
                }
                //显示需要显示的 Fragment
                show(showFragment)
            }
            //要显示的 Fragment 没添加过
            else {
                //如果正在显示的 Fragment 不为 null，且正在显示，则隐藏
                if (hideFragment != null) {
                    if (hideFragment.isVisible) {
                        hide(hideFragment)
                    }
                }
                //添加需要显示的 Fragment
                add(
                    containerViewId,
                    showFragment,
                    showFragment.asTag()
                )
            }
        }.commit()
    }

    /**
     * 当要显示的 Fragment 没有实例时，此时该 Fragment 肯定没有添加过
     */
    private fun hideAndShow(hideFragment: Fragment?, showFragmentClass: Class<out Fragment>) {
        manager.beginTransaction().apply {
            //如果正在显示的 Fragment 可见
            if (hideFragment != null) {
                if (hideFragment.isVisible) {
                    hide(hideFragment)
                }
            }
            //加载要显示的 Fragment
            add(
                containerViewId,
                showFragmentClass,
                Bundle(),
                showFragmentClass.asTag()
            )
        }.commit()
    }

}

/**
 * fragment 构建器
 */
class FragmentCreator internal constructor(
    private val manager: FragmentManager
) {
    private val fragmentClassList = mutableListOf<Class<out Fragment>>()
    private val fragmentMap = mutableMapOf<Class<out Fragment>, Fragment?>()
    private val creatorMap = mutableMapOf<Class<out Fragment>, Creator<out Fragment>?>()

    /**
     * 添加 Fragment
     */
    @JvmOverloads
    fun <T : Fragment> addFragment(
        fragmentClass: Class<T>,
        creator: Creator<T>? = null
    ): FragmentCreator {
        fragmentClassList.add(fragmentClass)
        fragmentMap[fragmentClass] = manager.findFragmentByTag(fragmentClass.asTag())
        creatorMap[fragmentClass] = creator
        return this
    }

    /**
     * 根据 Fragment Class 获取 Fragment 实例
     */
    internal fun obtainFragment(fragmentClass: Class<out Fragment>): Fragment? {
        var fragment = fragmentMap[fragmentClass]
        if (fragment == null) {
            //先从 manager 里面查找
            val target = manager.findFragmentByTag(fragmentClass.asTag())
            //不为 null，直接赋值
            if (target != null) {
                fragment = target
            }
            //为 null，让 creator 构建
            else {
                val creator = creatorMap[fragmentClass]
                //结果不为 null，则赋值
                if (creator != null) {
                    fragment = creator.createFragment()
                }
                //否则返回给外部处理
                else {
                    return null
                }
            }
            fragmentMap[fragmentClass] = fragment
        }
        return fragment
    }

    /**
     * Fragment 的数量
     */
    internal fun fragmentSize(): Int = fragmentClassList.size

    /**
     * 根据 index 获取 Fragment 的 Class
     */
    internal fun getFragmentClassByIndex(index: Int): Class<out Fragment> = fragmentClassList[index]

    /**
     * 根据 Fragment 的 Class 获取 对应的 index
     */
    internal fun getIndexByFragmentClass(fragmentClass: Class<out Fragment>): Int =
        fragmentClassList.indexOf(fragmentClass)

}

/**
 * 显示 Fragment 的扩展函数
 */
inline fun <reified T : Fragment> FragmentController.showFragment() {
    showFragment(T::class.java)
}

/**
 * 添加 Fragment 的扩展函数
 */
inline fun <reified T : Fragment> FragmentCreator.addFragment(crossinline block: () -> T) {
    addFragment(T::class.java, object : Creator<T> {
        override fun createFragment(): T = block.invoke()
    })
}

/**
 * 创建 Fragment
 */
interface Creator<T : Fragment> {
    fun createFragment(): T
}

private fun <F : Fragment> Class<F>.asTag(): String = "FragmentController$name"
private fun <F : Fragment> F.asTag(): String = javaClass.asTag()
