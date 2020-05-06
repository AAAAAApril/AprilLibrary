package com.april.library.trash;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import kotlin.Deprecated;

@Deprecated(message = "工具代码演示")
public class TrashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TrashViewModel viewModel = new ViewModelProvider(this, getDefaultViewModelProviderFactory())
                .get(TrashViewModel.class);

        /*
            观察刷新状态
         */
        viewModel.getStringLiveData().observerRefreshing(this, aBoolean -> {

        });
        /*
            观察加载更多状态
         */
        viewModel.getStringLiveData().observerLoadingMore(this, aBoolean -> {

        });
        /*
            观察请求状态
         */
        viewModel.getStringLiveData().observerRequesting(this, aBoolean -> {

        });
        /*
            观察数据
         */
        viewModel.getStringLiveData().observe(this, s -> {

        });

        //普通请求操作
        viewModel.doSomeThing();
        //刷新操作
        viewModel.doRefresh();
        //加载更多操作
        viewModel.doLoadMore();

    }
}
