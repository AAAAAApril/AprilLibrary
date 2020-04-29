package com.april.library.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * 和嵌套图表绑定的 ViewModel
 */
class InnerViewModel(application: Application) : AndroidViewModel(application) {
    var name: String = ""
    var number: String = ""
}