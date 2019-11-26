package com.april.develop.widget

import android.content.Context
import android.util.AttributeSet
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager


interface OnGridItemCreateListener<in T : Any> {

    /**
     * 创建 ItemView
     */
    fun onCreateGridItemView(parent: ViewGroup): View

    /**
     * 绑定数据
     */
    fun onBindGridItemView(
        holder: RecyclerView.ViewHolder,
        bean: T,
        selected: Boolean
    )
}

interface OnGridItemSelectChangeListener<in T : Any> {
    /**
     * 选中监听
     *
     * 注：这个函数的调用会在 Adapter 更新之前
     *
     * [selectedBean] 被选中的数据，null 表示没有一个选中
     */
    fun onGridItemSelectChanged(
        gridViewPager: GridViewPager<*>,
        selectedBean: T?
    )
}

interface OnGridItemCreateDecorationListener {
    /**
     * 给 item 创建装饰器
     *
     * [vertical] 是否是纵向
     */
    fun onGridItemCreateDecoration(
        context: Context,
        spanCount: Int,
        vertical: Boolean
    ): Array<RecyclerView.ItemDecoration>
}

/**
 * 网格 ViewPager
 */
class GridViewPager<T : Any> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    //礼物数据列
    private val mDataList = mutableListOf<T>()
    //adapter 列
    private val mAdapterList = mutableListOf<RecyclerViewAdapter<T>>()
    //选中数据列
    private val mCheckArray = SparseBooleanArray()
    //行（横向为行）
    private var mRowCount: Int = 2
    //列（纵向为列）
    internal var mColumnCount: Int = 4
    //item 创建监听
    internal var itemCreateListener: OnGridItemCreateListener<T>? = null
    //item 选中监听
    private var itemSelectChangeListener: OnGridItemSelectChangeListener<T>? = null
    //item 装饰器
    internal var itemCreateDecorationListener: OnGridItemCreateDecorationListener? = null
    //上一个选择的页面
    internal var mLastSelectedPageIndex = 0
    //页面切换之后取消选择
    internal var mUnSelectWhenPageChanged = true
    //当重复点击时，是否取消选中
    private var mClearSelectWhenReClick = true

    init {
        OnGridViewPagerPageChangeListener(this)
    }

    /**
     * 解决高度不能随内部数据高度自适应的问题
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mHeightMeasureSpec: Int
        var height = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val h = child.measuredHeight
            if (h > height) {
                //选高度最大的那个
                height = h
            }
        }
        mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            height,
            MeasureSpec.EXACTLY
        )
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }

    /**
     * 列数和行数（默认4列2行）
     */
    fun setColumnAndRowCount(columnCount: Int = mColumnCount, rowCount: Int = mRowCount) {
        mColumnCount = columnCount
        mRowCount = rowCount
    }

    /**
     * 在页面切换之后，取消之前一个选中
     */
    fun setUnSelectWhenPageChanged(doUnSelect: Boolean = mUnSelectWhenPageChanged) {
        mUnSelectWhenPageChanged = doUnSelect
    }

    /**
     * 设置重复点击时，是否取消选中
     */
    fun setClearSelectWhenReClick(doClear: Boolean = mClearSelectWhenReClick) {
        mClearSelectWhenReClick = doClear
    }

    /**
     * 设置 item 监听器
     */
    fun setOnGridItemCreateListener(itemCreateListener: OnGridItemCreateListener<T>) {
        this.itemCreateListener = itemCreateListener
    }

    /**
     * 设置 item 选中监听
     */
    fun setOnGridItemSelectChangeListener(
        itemSelectChangeListener: OnGridItemSelectChangeListener<T>
    ) {
        this.itemSelectChangeListener = itemSelectChangeListener
    }

    /**
     * 设置 item 装饰器
     */
    fun setOnGridItemCreateDecorationListener(
        itemCreateDecorationListener: OnGridItemCreateDecorationListener
    ) {
        this.itemCreateDecorationListener = itemCreateDecorationListener
    }

    /**
     * 获取选中的数据，可能为 null，表示没有选中任何一个数据
     */
    fun getSelectedBean(): T? {
        val index = mCheckArray.indexOfValue(true)
        if (index == -1) {
            return null
        }
        return mDataList[index]
    }

    /**
     * 取消选中的数据
     */
    fun clearSelectedBean() {
        val lastCheckedIndexInDataList = mCheckArray.indexOfValue(true)
        if (lastCheckedIndexInDataList == -1) {
            return
        }
        itemSelectChangeListener?.onGridItemSelectChanged(this, null)
        mCheckArray.put(mCheckArray.keyAt(lastCheckedIndexInDataList), false)
        notifyLastCheckedAdapterPosition(lastCheckedIndexInDataList)
    }

    /**
     * 滚动到选中的页面
     *
     * [Boolean] 是否滚动成功
     * 当被选中的页面不是正在展示的这个页面时，才会滚动成功，其他情况都是失败
     */
    fun scrollToSelectedPage(): Boolean {
        val lastCheckedIndexInDataList = mCheckArray.indexOfValue(true)
        //没有选中的项
        if (lastCheckedIndexInDataList == -1) {
            return false
        }
        val splitItemNum = mRowCount * mColumnCount
        val lastSize = lastCheckedIndexInDataList + 1
        val adapterIndex = if (lastSize % splitItemNum == 0) {
            (lastSize / splitItemNum) - 1
        } else {
            lastSize / splitItemNum
        }
        //选中的项正在展示
        if (currentItem == adapterIndex) {
            return false
        }
        //滚动到目标页面
        setCurrentItem(adapterIndex, true)
        return true
    }

    /**
     * 设置数据列
     * @return 创建的页面数
     */
    fun resetGridDataList(newDataList: MutableList<T>): Int {
        adapter = null
        mCheckArray.clear()
        mDataList.clear()
        mAdapterList.clear()

        if (newDataList.isEmpty()) {
            return 0
        }
        assert(itemCreateListener != null) {
            "必须设置 OnGridItemCreateListener"
        }
        mDataList.addAll(newDataList)
        for (index in newDataList.indices) {
            mCheckArray.put(index, false)
        }
        //拆分成组
        splitDataList(mRowCount * mColumnCount, newDataList) {
            mAdapterList.add(
                RecyclerViewAdapter(
                    this,
                    it
                )
            )
        }
        //ViewPager 设置 Adapter
        adapter = GridPagerAdapter(
            this,
            mAdapterList as MutableList<RecyclerViewAdapter<*>>
        )
        offscreenPageLimit = mAdapterList.size
        return mAdapterList.size
    }

    /**
     * item 被点击了
     */
    internal fun onItemClick(adapter: RecyclerViewAdapter<T>, bean: T) {
        //上一个选中的数据在所有数据列里面的下标
        val lastCheckedIndexInDataList = mCheckArray.indexOfValue(true)

        //现在选中的这个数据在 adapter 里面的下标
        val nowCheckedIndexInAdapter = adapter.itemBeanList.indexOf(bean)
        //现在选中的这个数据在 所有数据列 里面的下标
        val nowCheckedIndexInDataList =
            mRowCount * mColumnCount * mAdapterList.indexOf(adapter) + nowCheckedIndexInAdapter
        /*
            之前没有选中
         */
        if (lastCheckedIndexInDataList == -1) {
            itemSelectChangeListener?.onGridItemSelectChanged(this, bean)
            mCheckArray.put(mCheckArray.keyAt(nowCheckedIndexInDataList), true)
            adapter.notifyItemChanged(nowCheckedIndexInAdapter)
        }
        /*
            之前有选中的
         */
        else {
            /*
                上一个选中的就是现在点击的这个
             */
            if (lastCheckedIndexInDataList == nowCheckedIndexInDataList) {
                //重复点击时，如果取消选中
                if (mClearSelectWhenReClick) {
                    itemSelectChangeListener?.onGridItemSelectChanged(this, null)
                    mCheckArray.put(mCheckArray.keyAt(lastCheckedIndexInDataList), false)
                    adapter.notifyItemChanged(nowCheckedIndexInAdapter)
                }
                //不取消选中
                else {
                    itemSelectChangeListener?.onGridItemSelectChanged(this, bean)
                    adapter.notifyItemChanged(nowCheckedIndexInAdapter)
                }
            }
            /*
                更换了选择
             */
            else {
                itemSelectChangeListener?.onGridItemSelectChanged(this, bean)
                //更新之前的
                mCheckArray.put(mCheckArray.keyAt(lastCheckedIndexInDataList), false)
                notifyLastCheckedAdapterPosition(lastCheckedIndexInDataList)
                //更新现在的
                mCheckArray.put(mCheckArray.keyAt(nowCheckedIndexInDataList), true)
                adapter.notifyItemChanged(nowCheckedIndexInAdapter)
            }
        }

    }

    /**
     * 这个位置上的 item 是否被选中
     */
    internal fun isItemChecked(
        adapter: RecyclerViewAdapter<T>,
        adapterItemPosition: Int
    ): Boolean = mCheckArray.valueAt(
        mRowCount * mColumnCount * mAdapterList.indexOf(adapter) + adapterItemPosition
    )

    /**
     * 更新之前选中的位置
     */
    private fun notifyLastCheckedAdapterPosition(lastCheckedIndexInDataList: Int) {
        val splitItemNum = mRowCount * mColumnCount
        val lastSize = lastCheckedIndexInDataList + 1
        //余数
        val remainder = lastSize % splitItemNum
        //刚好除尽
        if (remainder == 0) {
            val adapterIndex = lastSize / splitItemNum
            val adapter = mAdapterList[adapterIndex - 1]
            val indexInAdapter = adapter.itemBeanList.lastIndex
            adapter.notifyItemChanged(indexInAdapter)
        }
        //未除尽
        else {
            val adapterIndex = (lastSize / splitItemNum)
            val adapter = mAdapterList[adapterIndex]
            val indexInAdapter = remainder - 1
            adapter.notifyItemChanged(indexInAdapter)
        }
    }

}

/**
 * 页面切换监听
 */
internal class OnGridViewPagerPageChangeListener(
    private val viewPager: GridViewPager<*>
) : ViewPager.OnPageChangeListener {
    init {
        viewPager.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (!viewPager.mUnSelectWhenPageChanged) {
            return
        }
        if (viewPager.mLastSelectedPageIndex == position) {
            return
        }
        viewPager.mLastSelectedPageIndex = position
        viewPager.clearSelectedBean()
    }
}

internal class RecyclerViewAdapter<T : Any>(
    private val viewPager: GridViewPager<T>,
    internal val itemBeanList: MutableList<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object :
            RecyclerView.ViewHolder(viewPager.itemCreateListener!!.onCreateGridItemView(parent)) {}
    }

    override fun getItemCount(): Int = itemBeanList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        viewPager.itemCreateListener!!.onBindGridItemView(
            holder,
            itemBeanList[holder.adapterPosition],
            viewPager.isItemChecked(this, holder.adapterPosition)
        )
        holder.itemView.setOnClickListener {
            viewPager.onItemClick(
                this,
                itemBeanList[holder.adapterPosition]
            )
        }
    }
}

internal class GridPagerAdapter(
    private val viewPager: GridViewPager<*>,
    private val adapterList: MutableList<RecyclerViewAdapter<*>>
) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val recyclerView = RecyclerView(container.context)
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        recyclerView.layoutManager = GridLayoutManager(
            container.context, viewPager.mColumnCount
        )
        recyclerView.adapter = adapterList[position]
        viewPager.itemCreateDecorationListener?.onGridItemCreateDecoration(
            recyclerView.context,
            viewPager.mColumnCount,
            true
        )?.map {
            recyclerView.addItemDecoration(it)
        }
        container.addView(recyclerView)
        return recyclerView
    }

    override fun getCount(): Int = adapterList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
//        container.removeView(`object` as View)
    }
}

/**
 * 拆分数据列
 */
internal fun <T> splitDataList(
    //每个分组多少个
    splitItemNum: Int,
    newDataList: MutableList<T>,
    block: (MutableList<T>) -> Unit
) {
    //刚好只有一组
    if (newDataList.size <= splitItemNum) {
        block.invoke(newDataList)
    }
    //有多组
    else {
        //被分成的组数
        val splitNum = if (newDataList.size % splitItemNum == 0) {
            //除尽的情况
            newDataList.size / splitItemNum
        } else {
            //未除尽的情况
            newDataList.size / splitItemNum + 1
        }
        for (i in 0 until splitNum) {
            block.invoke(
                if (i < splitNum - 1) {
                    newDataList.subList(
                        i * splitItemNum, (i + 1) * splitItemNum
                    )
                }
                //最后一组
                else {
                    newDataList.subList(
                        i * splitItemNum, newDataList.size
                    )
                }
            )
        }
    }
}