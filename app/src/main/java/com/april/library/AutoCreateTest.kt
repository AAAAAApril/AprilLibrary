package com.april.library

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.april.develop.ui.*
import kotlinx.android.synthetic.main.layout_auto_create_content.*
import kotlinx.android.synthetic.main.layout_auto_create_title.*

class AutoCreateTestActivity : AutoCreateActivity(),
    IInflateView by InflateView(
        R.layout.layout_auto_create_title,
        R.layout.layout_auto_create_content
    ) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lact_tv.text = "这里是Activity的标题"
        lacc_tv_top.text = "这里是Activity的顶部内容"
        lacc_tv_bottom.text = "这里是Activity的底部内容"
        //内嵌 Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.lacc_frame, AutoCreateTestFragment())
            .commit()
    }
}

class AutoCreateTestFragment : AutoCreateFragment(),
    IInflateView by InflateView(
        R.layout.layout_auto_create_title,
        R.layout.layout_auto_create_content
    ) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lact_tv.text = "这里是Fragment的标题"
        lacc_tv_top.text = "这里是Fragment的顶部内容"
        lacc_tv_bottom.text = "这里是Fragment的底部内容"
    }

    override fun inflateContentLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}