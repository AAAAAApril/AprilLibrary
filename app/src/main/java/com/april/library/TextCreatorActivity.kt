package com.april.library

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.style.DynamicDrawableSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.april.develop.helper.toast
import com.april.text.*
import com.april.text.dsl.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import kotlinx.android.synthetic.main.activity_text_creator.*

class TextCreatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_creator)
        atc_tv.apply {
            richText(
                TextCreator("hello world !", textColor = R.color.colorAccent),
                LineCreator(1),
                TextCreator("hello world !", backgroundColor = R.color.colorPrimary),
                LineCreator(),
                TextCreator("hello ", textSizeDP = 18),
                ImageCreator(
                    R.drawable.ic_launcher_foreground,
                    centerVertical = true,
                    onClick = {
                        toast("picture clicked !")
                    }),
                TextCreator(" world !", textSizeDP = 18),
                LineCreator(),
                TextCreator("hello world !",
                    textType = TextCreatorType.Serif,
                    onClick = { _, _ ->
                        toast("hello world !")
                    }
                ),
                highLightColor = R.color.colorAccent
            )
//            setOnClickListener {
            //这里的点击事件会覆盖掉 上面两个
//                toast("TextView！")
//            }
        }

        atc_tv1.richText(
            TextCreator("你"),
            ImageCreator(
                imageRes = R.drawable.icon_money_round_green,
                centerVertical = false
            ),
            TextCreator("好"),
            ImageCreator(
                imageDrawable = ContextCompat.getDrawable(this, R.drawable.icon_money_round_yellow),
                centerVertical = true
            ),
            TextCreator("呀")
        )

        act_tv2.spannableText {
            text {
                value = "你"
                textColor = getColorInt(R.color.colorAccent)
            }
            line(1)
            image {
                drawableValue = getDrawable(R.drawable.icon_money_round_yellow)
                centerVertical = true
            }
            line(1)
            text {
                value = "好"
                textColor = getColorInt(R.color.colorPrimary)
            }
            line(2)
            image {
                resourceValue = R.drawable.icon_money_round_green
                centerVertical = false
            }
            line()
            text {
                value = "呀"
                textColor = getColorInt(R.color.colorPrimaryDark)
            }
        }

    }
}