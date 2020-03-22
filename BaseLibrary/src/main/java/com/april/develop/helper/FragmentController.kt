package com.april.develop.helper

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.lang.NullPointerException

/**
 * 控制 Fragment 的显示与隐藏，并协助处理视图恢复
 */
class FragmentController(
    //show hide 模式，或者 replace 模式
    private val replaceMode: Boolean = false,
    //用于在状态暂存时，存储当前正在显示的 Fragment 的 Class name
    private val savedStateKey: String = "${FragmentController::class.java.name}_SavedStateKey"
) {
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
    fun onCreate(
        manager: FragmentManager,
        defaultSelectIndex: Int = this.showingIndex
    ): FragmentCreator {
        this.manager = manager
        showingIndex = defaultSelectIndex
        creator = FragmentCreator(manager)
        return creator
    }

    /**
     * ②
     * 设置 Fragment 显示的位置
     * [savedInstanceState] 处理视图恢复
     *
     * 视图恢复时，会默认显示曾经显示的 Fragment，不需要再手动显示
     *
     * @return 正在选中的位置
     */
    fun onViewCreated(@IdRes containerViewId: Int, view: View, savedInstanceState: Bundle?): Int {
        this.containerViewId = containerViewId
        var showingIndex = this.showingIndex
        savedInstanceState?.getString(savedStateKey)?.let { name ->
            showingFragmentClass = creator.getFragmentClassByClassName(name)
            showingIndex = creator.getIndexByFragmentClassName(name)
            this.showingIndex = showingIndex
        }
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
        if (showingFragmentClass != null) {
            outState.putString(savedStateKey, showingFragmentClass!!.name)
        }
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
        val target: Fragment = creator.obtainFragment(fragmentClass)
        manager.beginTransaction()
            .replace(containerViewId, target, target.asTag())
            .commit()
        showingFragmentClass = fragmentClass
    }

    /**
     * show hide 模式
     */
    private fun showAndHide(fragmentClass: Class<out Fragment>) {
        //要显示的 Fragment 正在显示
        if (fragmentClass == showingFragmentClass) {
            //正在显示的 Fragment
            val showingFragment: Fragment = creator.obtainFragment(fragmentClass)
            //如果正在显示的被隐藏了，则显示它
            if (showingFragment.isHidden) {
                manager.beginTransaction()
                    .show(showingFragment)
                    .commit()
            }
            return
        }
        //要显示的 Fragment 没有显示
        manager.beginTransaction().apply {
            //要显示的
            val targetFragment: Fragment = creator.obtainFragment(fragmentClass)
            //正在显示的
            val showingFragment: Fragment? = if (showingFragmentClass != null) {
                creator.obtainFragment(showingFragmentClass!!)
            } else {
                null
            }

            //要显示的 Fragment 是添加过的，此时正在显示的 Fragment 一定不为 null
            if (targetFragment.isAdded) {
                //如果正在显示的 Fragment 可见
                if (showingFragment?.isVisible == true) {
                    hide(showingFragment)
                }
                //显示需要显示的 Fragment
                show(targetFragment)
            }
            //要显示的 Fragment 没添加过
            else {
                //如果正在显示的 Fragment 不为 null，且正在显示，则隐藏
                if (showingFragment?.isVisible == true) {
                    hide(showingFragment)
                }
                //添加需要显示的 Fragment
                add(
                    containerViewId,
                    targetFragment,
                    targetFragment.asTag()
                )
            }
            showingFragmentClass = fragmentClass
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
    private val creatorMap = mutableMapOf<Class<out Fragment>, Creator<out Fragment>>()

    /**
     * 添加 Fragment
     */
    fun <T : Fragment> addFragment(
        fragmentClass: Class<T>,
        creator: Creator<T>
    ): FragmentCreator {
        fragmentClassList.add(fragmentClass)
        fragmentMap[fragmentClass] = manager.findFragmentByTag(fragmentClass.asTag())
        creatorMap[fragmentClass] = creator
        return this
    }

    /**
     * 根据 Fragment Class 获取 Fragment 实例
     */
    internal fun <T : Fragment> obtainFragment(fragmentClass: Class<T>): T {
        var fragment = fragmentMap[fragmentClass]
        if (fragment == null) {
            val creator = creatorMap[fragmentClass]
                ?: throw NullPointerException("Fragment ${fragmentClass.name} 对应的构建器 FragmentCreator 不能为 null")
            fragment = creator.createFragment()
            fragmentMap[fragmentClass] = fragment
        }
        return fragment as T
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

    /**
     * 根据 Fragment Class name 获取 Class
     */
    internal fun getFragmentClassByClassName(className: String): Class<out Fragment>? =
        fragmentClassList.find {
            it.name == className
        }

    /**
     * 根据 Fragment Class name 获取 index
     */
    internal fun getIndexByFragmentClassName(className: String): Int =
        fragmentClassList.indexOfFirst {
            it.name == className
        }
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

private fun <F : Fragment> Class<F>.asTag(): String = name
private fun <F : Fragment> F.asTag(): String = javaClass.asTag()

/**
 * Fragment Adapter for ViewPager
 */
class FragmentAdapter(
    manager: FragmentManager,
    private val destroyItem: Boolean = false
) : FragmentPagerAdapter(
    manager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private val titleList: MutableList<CharSequence> = mutableListOf()
    private val fragmentList: MutableList<Fragment> = mutableListOf()

    /**
     * 添加 fragment
     *
     * [title] 对应的标题，在 ViewPager 里面的时候可能会用到
     * [fragment] Fragment
     */
    fun addFragment(title: CharSequence? = null, fragment: Fragment): FragmentAdapter {
        titleList.add(title ?: "")
        fragmentList.add(fragment)
        return this
    }

    /**
     * 设置 标题
     */
    fun setTitles(vararg titles: CharSequence) {
        titleList.clear()
        titleList.addAll(titles)
    }

    /**
     * 设置 Fragment
     */
    fun setFragments(vararg fragments: Fragment) {
        fragmentList.clear()
        fragmentList.addAll(fragments)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (destroyItem) {
            super.destroyItem(container, position, `object`)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (titleList.size != fragmentList.size) {
            return null
        }
        return titleList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

}