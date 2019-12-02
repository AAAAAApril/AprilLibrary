package com.april.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.april.text.image
import com.april.text.line
import com.april.text.spannableText
import com.april.text.text
import kotlinx.android.synthetic.main.activity_text_creator.*

class TextCreatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_creator)
        act_tv2.spannableText {
            text {
                value = "你"
                textColor = getColorInt(R.color.colorAccent)
                onClick = { view, text ->
                    //do something
                }
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
                onClick = {
                    //do something
                }
            }
            line()
            text {
                value = "呀"
                textColor = getColorInt(R.color.colorPrimaryDark)
            }
        }
    }
}