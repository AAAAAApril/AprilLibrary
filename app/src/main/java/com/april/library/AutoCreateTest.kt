package com.april.library

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.april.develop.ui.*
import kotlinx.android.synthetic.main.layout_auto_create_content.view.*
import kotlinx.android.synthetic.main.layout_auto_create_title.view.*

class AutoCreateTestActivity : AutoCreateActivity(),
    ICreateTitleView by CreateTitleView(R.layout.layout_auto_create_title),
    ICreateContentView by CreateContentView(R.layout.layout_auto_create_content) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createdTitleView.lact_tv.text = "这里是Activity的标题"
        createdContentView.lacc_tv_top.text = "这里是Activity的顶部内容"
        createdContentView.lacc_tv_bottom.text = "这里是Activity的底部内容"
        //内嵌 Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.lacc_frame, AutoCreateTestFragment())
            .commit()
    }
}

class AutoCreateTestFragment : AutoCreateFragment(),
    ICreateTitleView by CreateTitleView(R.layout.layout_auto_create_title),
    ICreateContentView by CreateContentView(R.layout.layout_auto_create_content) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createdTitleView.lact_tv.text = "这里是Fragment的标题"
        createdContentView.lacc_tv_top.text = "这里是Fragment的顶部内容"
        createdContentView.lacc_tv_bottom.text = "这里是Fragment的底部内容"
    }

    /**
     * 可以自行实现，
     * 如果自行实现，则不会使用被委托的类里面的实现，
     * 在此处，则表示不会使用 [CreateContentView] 类里面的实现
     */
    override fun onCreateContentViewLayoutParams(parent: LinearLayout?): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}