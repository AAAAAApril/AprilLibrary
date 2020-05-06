package com.april.library.trash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.april.develop.vvm.StateLiveData
import com.april.develop.vvm.launch
import com.april.develop.vvm.loadMore
import com.april.develop.vvm.refresh

@Deprecated("工具代码演示")
class TrashViewModel(application: Application) : AndroidViewModel(application) {

    val stringLiveData by lazy {
        StateLiveData<String>()
    }

    fun doSomeThing() {
        launch {
            val result = "普通加载结果"
            stringLiveData.postValue(result)
        }
    }

    fun doRefresh() {
        refresh(stringLiveData) {
            return@refresh "刷新结果"
        }
    }

    fun doLoadMore() {
        loadMore(stringLiveData) {
            return@loadMore "加载更多结果"
        }
    }

}