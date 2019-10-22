package com.april.multiple

import android.content.Context
import android.view.View
import androidx.core.util.containsValue


/**
 * 使用 MultipleAdapter 时，最好是使用这个  Decoration
 * 它可以避免因为 header 和 footer 以及 placeholder 的特殊性而导致边距设置异常
 *
 * TODO 还有优化空间，优化集中在 HeaderFooterSupport，主要是优化判断是否是头尾布局
 */
class MultipleGridSpanDecoration(context: Context) : GridSpanDecoration(context) {

    private var support: HeaderFooterSupport? = null
    private var mIncludeSpecial: Boolean = false

    fun setHeaderFooterSupport(support: HeaderFooterSupport) {
        this.support = support
    }

    /**
     * 是否包括特殊的 Item，比如 头、尾，以及占位布局
     */
    fun setIncludeSpecial(includeSpecial: Boolean) {
        mIncludeSpecial = includeSpecial
    }

    override fun getSpanCount(itemView: View, position: Int): Int? {
        return when {
            /*
                头部、尾部，以及占位布局，都返回 1，这样在给它们做偏移时，就正常了
                这是因为在这个库中，头、尾、占位三种类型的布局都是拉通展示的，
                如果设置为不包括特殊布局，那么返回 null，表示不处理这些布局的偏移
             */
            support?.headerArray?.containsValue(itemView) == true -> return if (mIncludeSpecial) {
                1
            } else {
                null
            }
            support?.footerArray?.containsValue(itemView) == true -> return if (mIncludeSpecial) {
                1
            } else {
                null
            }
            itemView == support?.placeholderView -> return if (mIncludeSpecial) {
                1
            } else {
                null
            }
            else -> super.getSpanCount(itemView, position)
        }
    }

    override fun getHorizontalSpacing(itemView: View, position: Int): Int {
        return super.getHorizontalSpacing(itemView, position)
    }

    override fun getVerticalSpacing(itemView: View, position: Int): Int {
        return super.getVerticalSpacing(itemView, position)
    }

    override fun getIncludeEdge(itemView: View, position: Int): Boolean {
        return super.getIncludeEdge(itemView, position)
    }
}
