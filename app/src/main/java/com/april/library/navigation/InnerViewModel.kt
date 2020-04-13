package com.april.library.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class InnerViewModel(application: Application) : AndroidViewModel(application) {
    var name: String = ""
    var number: String = ""
}