package com.april.develop.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * 一个给 ViewPager 使用的 FragmentPagerAdapter
 */
class ViewPagerFragmentAdapter(manager: FragmentManager) : FragmentPagerAdapter(
    manager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private val titleTextList = mutableListOf<CharSequence?>()
    private val fragmentList = mutableListOf<Fragment?>()
    private val fragmentCreatorList = mutableListOf<() -> Fragment>()

    fun <T : Fragment> addFragment(
        titleText: CharSequence? = null,
        creator: () -> T
    ) {
        titleTextList.add(titleText)
        fragmentCreatorList.add(creator)
        fragmentList.add(null)
    }

    override fun getItem(position: Int): Fragment {
        var fragment = fragmentList[position]
        if (fragment != null) {
            return fragment
        }
        fragment = fragmentCreatorList[position].invoke()
        fragmentList[position] = fragment
        return fragment
    }

    override fun getCount(): Int = fragmentCreatorList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return titleTextList[position]
    }
}
