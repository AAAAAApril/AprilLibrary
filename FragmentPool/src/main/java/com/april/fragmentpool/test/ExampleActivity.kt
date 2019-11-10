package com.april.fragmentpool.test

import androidx.appcompat.app.AppCompatActivity
import com.april.fragmentpool.IActivitySupport
import com.april.fragmentpool.IActivitySupportImpl

class ExampleActivity : AppCompatActivity(), IActivitySupport by IActivitySupportImpl() {

    override fun onBackPressed() {
        super<IActivitySupport>.onBackPressed()
    }
}