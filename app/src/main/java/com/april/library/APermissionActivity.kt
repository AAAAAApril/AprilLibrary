package com.april.library

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.april.develop.helper.toast
import com.april.permission.APermission
import kotlinx.android.synthetic.main.activity_permission.*

class APermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        ap_btn.setOnClickListener {
            APermission.permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )?.granted {
                toast("通过了权限")
            }?.denied {
                toast("拒绝了权限")
            }?.request()
        }
    }
}