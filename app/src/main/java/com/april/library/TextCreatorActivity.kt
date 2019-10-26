package com.april.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.april.develop.helper.toast
import com.april.text.*
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
                    vertical = true,
                    onClick = {
                        toast("picture clicked !")
                    }),
                TextCreator(" world !", textSizeDP = 18),
                LineCreator(),
                TextCreator("hello world !",
                    textType = TextCreatorType.Serif,
                    onClick = {
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
    }
}