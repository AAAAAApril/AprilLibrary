package com.april.library

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.april.banner.CustomLayoutManagerInfinite
import com.april.banner.LoopLayoutManager
import com.april.banner.LoopPagerSnapHelper
import com.april.multiple.DefaultItemDelegate
import com.april.multiple.MultipleAdapter
import com.april.multiple.only
import com.april.network.glide.circleImage
import com.april.network.glide.roundImage
import kotlinx.android.synthetic.main.activity_banner.*
import kotlinx.android.synthetic.main.item_banner.view.*

/**
 * Banner 演示
 */
class BannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        ab_rv.apply {
            //设置横向循环滑动支持的布局管理器
//            layoutManager = LoopLayoutManager()
            layoutManager = CustomLayoutManagerInfinite()
            //设置循环滑动支持的 SnapHelper
//            LoopPagerSnapHelper().attachToRecyclerView(this)
        }.adapter = MultipleAdapter().apply {
            //item 布局代理
            only(object : DefaultItemDelegate<String>(R.layout.item_banner) {
                override fun onBindItemView(itemView: View, bean: String, adapterPosition: Int) {
                    itemView.apply {
                        if (adapterPosition % 2 == 0) {
                            circleImage(
                                bean,
                                ib_img
                            )
                        } else {
                            roundImage(
                                bean,
                                10,
                                ib_img
                            )
                        }
                    }
                }
            })
            //设置 5 个图片链接
            resetDataList(
                listOf(
                    "http://img1.imgtn.bdimg.com/it/u=1726759231,517049274&fm=26&gp=0.jpg",
                    "http://img2.imgtn.bdimg.com/it/u=4083906753,496324149&fm=26&gp=0.jpg",
                    "http://img1.imgtn.bdimg.com/it/u=1745873143,4143396340&fm=26&gp=0.jpg",
                    "http://img2.imgtn.bdimg.com/it/u=3659182505,1129011805&fm=26&gp=0.jpg",
                    "http://img0.imgtn.bdimg.com/it/u=4150948260,336816538&fm=26&gp=0.jpg"
                )
            )
        }
    }
}