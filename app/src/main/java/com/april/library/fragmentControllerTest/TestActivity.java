package com.april.library.fragmentControllerTest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.april.develop.helper.Creator;
import com.april.develop.helper.FragmentController;
import com.april.develop.helper.FragmentCreator;
import com.april.library.R;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

@Deprecated
public class TestActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener {

    private FragmentController controller;

    private FragmentController getController() {
        if (controller == null) {
            //不使用 replace 模式，而是 show hide 模式
            controller = new FragmentController(false);
        }
        return controller;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化，并设置默认选中的位置为 0，然后返回 FragmentCreator
        FragmentCreator creator = getController().onCreate(
                getSupportFragmentManager(),
                savedInstanceState,
                0
        );
        //添加 Fragment
        creator.addFragment(Content0Fragment.class, new Creator<Content0Fragment>() {
            @NotNull
            @Override
            public Content0Fragment createFragment() {
                return new Content0Fragment();
            }
        });
        // Activity 设置布局
        setContentView(R.layout.layout_fragment_controller_test);
        // Activity 设置布局之后，controller 处理视图恢复，并返回正在显示的位置
        int showingIndex = getController().onViewCreated(R.id.lfct_frame);
        /**
         * 其他初始化以及业务逻辑
         */
        // TabLayout 设置默认选中的 tab
        TabLayout layout = findViewById(R.id.lfct_tab);
        layout.addOnTabSelectedListener(this);
        TabLayout.Tab tab = layout.getTabAt(showingIndex);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //暂存数据
        getController().onSaveInstanceState(outState);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        getController().showFragment(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
