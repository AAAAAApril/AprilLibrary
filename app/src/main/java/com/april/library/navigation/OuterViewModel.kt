package com.april.library.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.april.develop.vvm.SingleLiveData

class OuterViewModel(application: Application) : AndroidViewModel(application) {

    val nameLiveData by lazy {
        //虽然这里用了 SingleLiveData ，但是在嵌套图表内发送消息，同样能够按照预期回调到观察的地方
        SingleLiveData<String>()
    }

    val numberLiveData by lazy {
        SingleLiveData<String>()
    }

}