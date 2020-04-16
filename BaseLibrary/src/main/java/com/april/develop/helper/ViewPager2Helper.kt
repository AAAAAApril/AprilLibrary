package com.april.develop.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * 使用 ViewPager2 时，与 TabLayoutMediator 结合使用，需要一些处理
 */
class ViewPager2Helper: TabLayoutMediator.TabConfigurationStrategy {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var mediator: TabLayoutMediator
    private var strategy: TabLayoutMediator.TabConfigurationStrategy? = null

    /**
     * 设置 ViewPager2
     */
    fun setViewPager2(
        viewPager2: ViewPager2,
        tabLayout: TabLayout,
        strategy: TabLayoutMediator.TabConfigurationStrategy? = null
    ) {
        this.strategy = strategy
        this.viewPager2 = viewPager2
        this.tabLayout = tabLayout
        this.mediator = TabLayoutMediator(tabLayout, viewPager2, this)
    }

    /**
     * 建立联系
     */
    fun attach(adapter: FragmentStateAdapter) {
        viewPager2.adapter = adapter
        mediator.attach()
    }

    /**
     * 解除联系
     */
    fun detach() {
        mediator.detach()
        viewPager2.adapter = null
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        strategy?.onConfigureTab(tab, position)
    }

}

class ViewPager2FragmentAdapter : FragmentStateAdapter {

    constructor(fragment: Fragment) : super(fragment)

    constructor(activity: FragmentActivity) : super(activity)

    private val fragmentCreatorList = mutableListOf<ViewPager2FragmentCreator>()

    fun addFragment(creator: ViewPager2FragmentCreator) {
        fragmentCreatorList.add(creator)
    }

    override fun getItemCount(): Int {
        return fragmentCreatorList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentCreatorList[position].invoke()
    }

}

typealias ViewPager2FragmentCreator = () -> Fragment