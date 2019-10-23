package com.april.library

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.april.develop.ui.startContractIntent
import com.april.multiple.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_0.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MultipleAdapter()
        am_rv.layoutManager = LinearLayoutManager(this)
        am_rv.addItemDecoration(GridSpanDecoration(this).apply {
            setHorizontalSpacingDP(10)
            setVerticalSpacingDP(16)
            setIncludeEdge(true)
        })
        am_rv.adapter = adapter
        adapter.only(object : AbsItemDelegate<ItemBean>(R.layout.item_0) {
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, bean: ItemBean) {
                holder.itemView.i0_tv.text = bean.text
                holder.itemView.setOnClickListener {
                    bean.onClick.invoke(bean)
                }
            }
        })

        adapter.resetDataList(
            mutableListOf(
                ItemBean("TextCreator") {
                    startContractIntent(Intent(this, TextCreatorActivity::class.java))
                },
                ItemBean("APermission") {
                    startContractIntent(Intent(this, APermissionActivity::class.java))
                },
                ItemBean("TextLengthWatcher") {
                    startContractIntent(Intent(this, TextLengthWatcherActivity::class.java))
                },
                ItemBean("MultipleItem") {
                    startContractIntent(Intent(this, MultipleItemActivity::class.java))
                }
            )
        )

//        frame.addChild(
//            child = createLinearLayout(false, childrenGravity = Gravity.CENTER).apply {
//                backgroundColorRes(R.color.colorPrimary)
//                addChild(child = createTextView {
//                    textView0 = this
//                    text = "左侧文字控件"
//                    textColorRes(R.color.colorAccent)
//                    setPadding(30)
//                })
//                addChild(
//                    child = FrameLayout(context).apply {
//                        addChild(child = createTextView {
//                            textView1 = this
//                            text = "中间文字控件"
//                        }, gravity = Gravity.CENTER)
//                    }, width = 0,
//                    height = ViewGroup.LayoutParams.MATCH_PARENT,
//                    weight = 1f
//                )
//                addChild(child = createButton {
//                    button = this
//                    text = "右侧按钮控件"
//                    backgroundTintListValueOfColorRes(R.color.colorAccent)
//                    textColorRes(R.color.colorPrimaryDark)
//                    setOnClickListener {
//                        toast("点击了右侧按钮")
//                    }
//                })
//            }
//        )

//        startContractIntent(Intent()) { _, _ ->
//
//        }

//        RuntimeDataManager.compare<MainActivity> { new, old ->
//            return@compare new != old
//        }
//        RuntimeDataManager.listen(true, object : RuntimeDataListener<MainActivity> {
//            override fun onDataNotify(new: MainActivity?, old: MainActivity?) {
//
//            }
//        })
    }
}

internal class ItemBean(
    val text: CharSequence,
    val onClick: (ItemBean) -> Unit
)