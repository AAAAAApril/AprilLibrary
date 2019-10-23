package com.april.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.april.develop.helper.toast
import com.april.develop.watcher.listenTextViewChange
import kotlinx.android.synthetic.main.activity_text_length_watcher.*

class TextLengthWatcherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_length_watcher)
        listenTextViewChange()
            .withView(atlw_3_7, 3, 7)
            .withView(atlw_6_9, 6, 9)
            .withView(atlw_7, 7, 7)
            .withView(atlw_2_5, 2, 5)
            .withView(atlw_9_11, 9, 11)
            .listen {
                atlw_btn.isEnabled = it
            }
        atlw_btn.setOnClickListener {
            toast("点击成功了！")
        }
    }
}