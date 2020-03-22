package com.april.develop.helper

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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