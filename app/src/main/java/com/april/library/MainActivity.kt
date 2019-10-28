package com.april.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.april.develop.ui.startContractIntent
import com.april.multiple.DefaultItemDelegate
import com.april.multiple.GridSpanDecoration
import com.april.multiple.MultipleAdapter
import com.april.multiple.only
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
        adapter.only(object : DefaultItemDelegate<ItemBean>(R.layout.item_0) {
            override fun onBindItemView(itemView: View, bean: ItemBean, itemAdapterPosition: Int) {
                super.onBindItemView(itemView, bean, itemAdapterPosition)
                itemView.i0_tv.text = bean.text
                itemView.setOnClickListener {
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

    }
}

internal class ItemBean(
    val text: CharSequence,
    val onClick: (ItemBean) -> Unit
)