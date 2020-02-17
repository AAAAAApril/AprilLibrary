package com.april.develop.helper

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * 控制 Fragment 的显示与隐藏，并协助处理视图恢复
 */
class FragmentController(
    private val fragmentClassArray: Array<Class<out Fragment>>,
    //show hide 模式，或者 replace 模式
    private val replaceMode: Boolean = false,
    //用于在状态暂存时，存储当前正在显示的 Fragment 的 Class name
    private val savedStateKey: String = "${FragmentController::class.java.name}_SavedStateKey"
) {

    private lateinit var manager: FragmentManager
    private val fragmentList = mutableListOf<Fragment>()
    @IdRes
    private var containerViewId: Int = View.NO_ID
    private var showingFragmentClass: Class<out Fragment>? = null

    /**
     * ①
     * 关联到 FragmentManger
     */
    fun attachFragmentManager(
        manager: FragmentManager,
        creator: FragmentCreator
    ) {
        this.manager = manager
        fragmentList.clear()
        fragmentClassArray.forEachIndexed { index, clazz ->
            fragmentList.add(
                manager.findFragmentByTag(clazz.asTag()) ?: creator.onControllerCreateFragment(
                    clazz, index
                )
            )
        }
    }

    /**
     * ②
     * 设置 Fragment 显示的位置
     * [savedInstanceState] 处理视图恢复
     */
    fun setContainerViewId(@IdRes containerViewId: Int, savedInstanceState: Bundle?) {
        this.containerViewId = containerViewId
        savedInstanceState?.getString(savedStateKey)?.let { name ->
            showingFragmentClass = fragmentClassArray.find { it.name == name }
        }
    }

    /**
     * ③
     * 显示一个 Fragment
     */
    fun <F : Fragment> showFragment(fragmentClass: Class<F>) {
        //要显示的 Fragment 正在显示
        if (replaceMode) {
            if (fragmentClass == showingFragmentClass) {
                return
            }
            val target: Fragment = fragmentList[fragmentClassArray.indexOf(fragmentClass)]
            manager.beginTransaction()
                .replace(containerViewId, target, target.asTag())
                .commit()
            showingFragmentClass = fragmentClass
        } else {
            if (fragmentClass == showingFragmentClass) {
                //正在显示的 Fragment
                val showingFragment: Fragment =
                    fragmentList[fragmentClassArray.indexOf(showingFragmentClass)]
                //如果正在显示的被隐藏了，则显示它
                if (showingFragment.isHidden) {
                    manager.beginTransaction()
                        .show(showingFragment)
                        .commit()
                }
                return
            }
            manager.beginTransaction().apply {
                //要显示的
                val targetFragment: Fragment =
                    fragmentList[fragmentClassArray.indexOf(fragmentClass)]
                //正在显示的
                val showingFragment: Fragment? =
                    if (showingFragmentClass == null) {
                        null
                    } else {
                        fragmentList[fragmentClassArray.indexOf(showingFragmentClass)]
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
     * ③
     * 显示该位置上对应的 Fragment
     */
    fun showFragment(index: Int) {
        showFragment(fragmentClassArray[index])
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
    fun getFragmentCount(): Int = fragmentClassArray.size

}

interface FragmentCreator {
    /**
     * 创建 Fragment
     */
    fun onControllerCreateFragment(
        fragmentClass: Class<out Fragment>,
        index: Int
    ): Fragment
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