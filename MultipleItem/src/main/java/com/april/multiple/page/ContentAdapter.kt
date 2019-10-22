package com.april.multiple.page

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.util.isEmpty
import androidx.recyclerview.widget.RecyclerView

/**
 * 用于创建普通页面
 *
 * 利用多样式，处理页面各种状态下的展示情况，比如：没网络、没数据，已经正常展示的情况 等
 */
class ContentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //内容布局（布局资源 ID 为 key，布局实例为 value）
    private val contentViewArray = SparseArray<View>()
    /*
        异常布局（布局资源 ID 为 key，布局实例为 value）
        应该不存在异常情况的布局也是很大一堆的情况吧，真要遇到了，也不应该用这个类了。
        由于异常布局可以切换
        因此，异常布局每一次只能展示一个
        只能手动切换异常布局（根据 exceptionKey，使用 showExceptionViews 函数）
     */
    private val exceptionViewArray = SparseArray<View>()

    //当前是否正处于异常状态（默认就是异常状态）
    private var exceptionModel = true
    //异常状态时的 key
    private var exceptionKey = -1

    override fun getItemCount(): Int {
        return if (exceptionModel) {
            //每次只展示一个异常布局
            if (exceptionViewArray.isEmpty()) {
                0
            } else {
                1
            }
        } else {
            contentViewArray.size()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (exceptionModel) {
            if (exceptionKey != -1) {
                exceptionKey
            } else {
                exceptionViewArray.keyAt(0)
            }
        } else {
            contentViewArray.keyAt(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            if (exceptionModel) {
                exceptionViewArray.get(viewType)
            } else {
                contentViewArray.get(viewType)
            }
        ) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    /**
     * @return  创建内容布局
     */
    fun createContentView(
        pageView: RecyclerView,
        @LayoutRes normalViewLayoutRes: Int
    ): View {
        val contentView = LayoutInflater.from(pageView.context)
            .inflate(normalViewLayoutRes, pageView, false)
        contentViewArray.put(normalViewLayoutRes, contentView)
        return contentView
    }

    /**
     * @return 创建异常布局
     */
    fun createExceptionView(
        pageView: RecyclerView,
        @LayoutRes exceptionViewLayoutRes: Int
    ): View {
        val exceptionView = LayoutInflater.from(pageView.context)
            .inflate(exceptionViewLayoutRes, pageView, false)
        exceptionViewArray.put(exceptionViewLayoutRes, exceptionView)
        return exceptionView
    }

    /**
     * 展示内容布局
     */
    fun showContentViews() {
        //已经是正常布局状态，则不处理
        if (!exceptionModel) {
            return
        }
        exceptionModel = false
        notifyDataSetChanged()
    }

    /**
     * 展示异常布局
     *
     * @param exceptionKey key，其实就是异常布局的 ID ，如果不传，则默认取第一个异常布局
     */
    @JvmOverloads
    fun showExceptionViews(exceptionKey: Int = -1) {
        //新切换的状态和当前状态相同，则不处理
        if (exceptionModel
            && this.exceptionKey == exceptionKey
        ) {
            return
        }
        this.exceptionKey = exceptionKey
        exceptionModel = true
        notifyDataSetChanged()
    }

}