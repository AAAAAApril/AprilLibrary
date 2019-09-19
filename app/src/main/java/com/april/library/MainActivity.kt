package com.april.library

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.april.develop.helper.*
import com.april.develop.ui.startContractIntent
import com.april.develop.watcher.*
import com.april.permission.Permission
import com.april.text.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var textView0: TextView
    private lateinit var textView1: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv?.multiText(
            TextCreator("hello world !", textColor = R.color.colorAccent),
            NewLine(),
            TextCreator("hello world !", backgroundColor = R.color.colorPrimary),
            NewLine(),
            TextCreator("hello ", textSizeDP = 18),
            ImageCreator(
                R.drawable.ic_launcher_foreground,
                vertical = true,
                onClick = {
                    toast("picture clicked !")
                }),
            TextCreator(" world !", textSizeDP = 18),
            NewLine(),
            TextCreator("hello world !",
                textType = TextCreatorType.Serif,
                highLightColor = R.color.colorAccent,
                onClick = {
                    toast("hello world !")
                }
            )
        )

        frame.addChild(
            child = createLinearLayout(false, childrenGravity = Gravity.CENTER).apply {
                backgroundColorRes(R.color.colorPrimary)
                addChild(child = createTextView {
                    textView0 = this
                    text = "左侧文字控件"
                    textColorRes(R.color.colorAccent)
                    setPadding(30)
                })
                addChild(child = FrameLayout(context).apply {
                    addChild(child = createTextView {
                        textView1 = this
                        text = "中间文字控件"
                    }, gravity = Gravity.CENTER)
                }, width = 0,
                    height = ViewGroup.LayoutParams.MATCH_PARENT,
                    weight = 1f
                )
                addChild(child = createButton {
                    button = this
                    text = "右侧按钮控件"
                    backgroundTintListValueOfColorRes(R.color.colorAccent)
                    textColorRes(R.color.colorPrimaryDark)
                    setOnClickListener {
                        toast("点击了右侧按钮")
                    }
                })
            }
        )

        Permission.permissions(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )?.granted {
            toast("通过了权限")
        }?.denied {
            toast("拒绝了权限")
        }?.request()

        TextStateWatcher.newInstance(supportFragmentManager)
            .addView(textView0, 9, 31)
            .addView(textView1, -1, 99)
            .addView(button, 8, -1)
            .watch {

            }

        startContractIntent(Intent()) { _, _ ->

        }

        DataManager.INSTANCE.compare<MainActivity> { new, old ->
            return@compare new != old
        }
        DataListenerImpl.get<MainActivity>(this).listen { new, old ->

        }
    }
}
