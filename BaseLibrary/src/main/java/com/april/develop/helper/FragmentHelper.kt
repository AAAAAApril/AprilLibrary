package com.april.develop.helper

import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * 便捷操作 Fragment 的显隐
 */
class FragmentHelper(
    //manager
    private val fragmentManager: FragmentManager,
    //装载位置
    @IdRes private val containerViewId: Int,
    //是否是 replace 方式，由于 replace 和 add show hide 不能用，所以可以给这个参数
    private val replaceMode: Boolean = false
) {
    //当前正在展示的 Fragment
    private var nowFragment: Fragment? = null

    /**
     * [Fragment] 获取当前正在展示的 Fragment
     */
    fun now(): Fragment? {
        return nowFragment
    }

    /**
     * [target] 显示一个 Fragment
     */
    fun show(target: Fragment) {
        if (replaceMode) {
            replaceFragment(target)
        } else {
            addShowFragment(target)
        }
    }

    private fun replaceFragment(replace: Fragment) {
        if (nowFragment == replace) {
            return
        }
        fragmentManager.beginTransaction()
            .replace(containerViewId, replace)
            .commitAllowingStateLoss()
        nowFragment = replace
    }

    private fun addShowFragment(show: Fragment) {
        if (nowFragment == show) {
            if (nowFragment!!.isHidden) {
                fragmentManager.beginTransaction()
                    .show(nowFragment!!)
                    .commitAllowingStateLoss()
            }
            return
        }

        val transaction = fragmentManager.beginTransaction()
        if (show.isAdded) {
            if (nowFragment!!.isVisible) {
                transaction
                    .hide(nowFragment!!)
            }
            transaction.show(show)
        } else {
            if (nowFragment != null) {
                if (nowFragment!!.isVisible) {
                    transaction
                        .hide(nowFragment!!)
                }
            }
            transaction
                .add(containerViewId, show)
        }
        transaction.commitAllowingStateLoss()
        nowFragment = show
    }

}


class FragmentAdapter(
    manager: FragmentManager,
    private val destroyItem: Boolean = false
) : FragmentPagerAdapter(manager) {

    private val titleList: MutableList<CharSequence> = mutableListOf()
    private val fragmentList: MutableList<Fragment> = mutableListOf()

    /**
     * 添加 fragment
     *
     * [title] 对应的标题，在 ViewPager 里面的时候可能会用到
     * [fragment] Fragment
     */
    fun addFragment(title: CharSequence? = null, fragment: Fragment): FragmentAdapter {
        title?.let { titleList.add(it) }
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