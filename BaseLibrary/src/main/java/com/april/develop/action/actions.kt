package com.april.develop.action

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 视图构建实体
 */
class ActionBean(
    //图标
    @DrawableRes
    var icon: Int = 0,
    //副图标
    @DrawableRes
    var subIcon: Int = 0,
    //标题
    var title: CharSequence = "",
    //副标题
    var subTitle: CharSequence = "",
    //内容
    var content: CharSequence = "",
    //内容提示
    var contentHint: CharSequence = "",
    //携带的参数
    var intent: Intent = Intent(),
    //目标 Activity
    var targetActivity: Class<*>? = null,
    //是否可操作（优先级最高）
    var enable: Boolean = true,
    //操作事件（自定义事件，当可操作，且 targetActivity 等于 null 的时候生效）
    var action: OnActionListener? = null
)

/**
 * 操作事件
 */
interface OnActionListener {

    /**
     * [item] 对应的 item View
     * [bean] 对应的数据实体
     * [position] 对应的位置
     */
    fun onAction(item: View, bean: ActionBean, position: Int)
}

/**
 * adapter
 */
class ActionAdapter(@LayoutRes private val itemLayoutRes: Int) : RecyclerView.Adapter<ActionViewHolder>() {

    private val actionBeanList: MutableList<ActionBean> = ArrayList()

    fun resetActions(actions: Array<ActionBean>?) {
        actionBeanList.clear()
        actions?.run {
            actionBeanList.addAll(this)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        return ActionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(itemLayoutRes, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return actionBeanList.size
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        actionBeanList[position].run {
            if (!enable) {
                return
            }
            holder.itemView.setOnClickListener {
                targetActivity?.let { target ->
                    intent.apply {
                        setClass(it.context, target)
                        it.context.startActivity(intent)
                    }
                    return@setOnClickListener
                }
                action?.onAction(it, actionBeanList[position], position)
            }
        }
    }

}

/**
 * viewHolder
 */
class ActionViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView)
