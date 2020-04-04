package com.april.library.fragmentControllerTest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.april.develop.helper.Creator;
import com.april.develop.helper.FragmentController;
import com.april.develop.helper.FragmentCreator;
import com.april.library.R;

import org.jetbrains.annotations.NotNull;

@Deprecated
public class TestFragment extends Fragment {

    private FragmentController controller;

    private FragmentController getController() {
        if (controller == null) {
            controller = new FragmentController();
        }
        return controller;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentCreator creator = getController().onCreate(
                getChildFragmentManager(),
                savedInstanceState
        );
        //如果不传 Fragment 构建接口，或者传 null，则表示将 Fragment 的创建委托给 FragmentManager
        creator.addFragment(Content0Fragment.class);
        creator.addFragment(Content1Fragment.class, null);
        /**
         *  使用 Creator 接口自己创建 Fragment 实例，可以更方便的传递 argument，
         *
         *      无论是在 Activity 还是 Fragment 中，onCreate 函数都能获得传递过来的值，
         *      而如果需要的参数来自网络，则完全可以使用 ViewModel + LiveData 做数据同步。
         *
         *      如果是要根据网络返回的数据动态创建 Fragment。自己想办法。
         */
        creator.addFragment(Content2Fragment.class, new Creator<Content2Fragment>() {
            @NotNull
            @Override
            public Content2Fragment createFragment() {
                Content2Fragment fragment = new Content2Fragment();
                fragment.setArguments(new Bundle());
                return fragment;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_controller_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getController().onViewCreated(R.id.lfct_frame);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getController().onSaveInstanceState(outState);
    }


}
