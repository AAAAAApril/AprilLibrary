package com.april.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.april.multiple.MultipleAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MultipleItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MultipleAdapter()
        am_rv.adapter = adapter

        /**
         * TODO
         */

    }
}