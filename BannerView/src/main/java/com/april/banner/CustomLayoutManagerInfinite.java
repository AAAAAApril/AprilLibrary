package com.april.banner;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 界面无线循环的LayoutManager
 */
public class CustomLayoutManagerInfinite extends RecyclerView.LayoutManager {
    private final String TAG = CustomLayoutManagerInfinite.class.getSimpleName();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    //    1 在RecyclerView初始化时，会被调用两次。
//    2 在调用adapter.notifyDataSetChanged()时，会被调用。
//    3 在调用setAdapter替换Adapter时,会被调用。
//    4 在RecyclerView执行动画时，它也会被调用。
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d(TAG, "onLayoutChildren ");
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        //state.isPreLayout()是支持动画的
        if (getItemCount() == 0 && state.isPreLayout()) {
            return;
        }
        //将当前Recycler中的view全部移除并放到报废缓存里,之后优先重用缓存里的view
        detachAndScrapAttachedViews(recycler);

        int actualHeight = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View scrap = recycler.getViewForPosition(i);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);
            int width = getDecoratedMeasuredWidth(scrap);
            int height = getDecoratedMeasuredHeight(scrap);
            layoutDecorated(scrap, 0, actualHeight, width, actualHeight + height);
            actualHeight += height;
            //超出界面的就不画了,也不add了
            if (actualHeight > getHeight()) {
                break;
            }
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d("feifeifei", "getChildCount() " + getChildCount() + " recycler.getScrapList().size() " + recycler.getScrapList().size());

        //界面向下滚动的时候,dy为正,向上滚动的时候dy为负

        //填充
        fill(dy, recycler, state);
        //滚动
        offsetChildrenVertical(dy * -1);

        //回收已经离开界面的
        recycleOut(dy, recycler, state);

        return dy;
    }


    private void fill(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //向下滚动
        if (dy > 0) {
            //先在底部填充
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView == null) {
                return;
            }
            int lastPos = getPosition(lastView);
            if (lastView.getBottom() - dy < getHeight()) {
                View scrap;
                if (lastPos == getItemCount() - 1) {
                    scrap = recycler.getViewForPosition(0);
                } else {
                    scrap = recycler.getViewForPosition(lastPos + 1);
                }
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(scrap, 0, lastView.getBottom(), width, lastView.getBottom() + height);
            }
        } else {
            //向上滚动
            //现在顶部填充
            View firstView = getChildAt(0);
            if (firstView == null) {
                return;
            }
            int layoutPostion = getPosition(firstView);

            if (firstView.getTop() >= 0) {
                View scrap;
                if (layoutPostion == 0) {
                    scrap = recycler.getViewForPosition(getItemCount() - 1);
                } else {
                    scrap = recycler.getViewForPosition(layoutPostion - 1);
                }
                addView(scrap, 0);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(scrap, 0, firstView.getTop() - height, width, firstView.getTop());
            }
        }
    }

    private void recycleOut(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == null) {
                continue;
            }
            if (dy > 0) {
                if (view.getBottom() - dy < 0) {
                    Log.d("feifeifei", "recycleOut " + i);
                    removeAndRecycleView(view, recycler);
                }
            } else {
                if (view.getTop() - dy > getHeight()) {
                    Log.d("feifeifei", "recycleOut " + i);
                    removeAndRecycleView(view, recycler);
                }
            }
        }
    }
}
