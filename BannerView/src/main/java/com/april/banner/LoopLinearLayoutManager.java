package com.april.banner;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 界面无线循环的LayoutManager
 */
public class LoopLinearLayoutManager extends RecyclerView.LayoutManager {
    private final String TAG = LoopLinearLayoutManager.class.getSimpleName();

    private boolean vertical;

    public LoopLinearLayoutManager() {
        this(false);
    }

    public LoopLinearLayoutManager(boolean vertical) {
        this.vertical = vertical;
    }

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
        if (getItemCount() < 1) {
            return;
        }
        //state.isPreLayout()是支持动画的
        if (state.isPreLayout()) {
            return;
        }
        //将当前Recycler中的view全部移除并放到报废缓存里,之后优先重用缓存里的view
        detachAndScrapAttachedViews(recycler);

        if (vertical) {
            int actualHeight = 0;
            for (int i = 0; i < getItemCount(); i++) {
                View scrap = recycler.getViewForPosition(i);
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(
                        scrap,
                        0,
                        actualHeight,
                        width,
                        actualHeight + height
                );
                actualHeight += height;
                //超出界面的就不画了,也不add了
                if (actualHeight > getHeight()) {
                    break;
                }
            }
        } else {
            int actualWidth = 0;
            for (int i = 0; i < getItemCount(); i++) {
                View scrap = recycler.getViewForPosition(i);
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(
                        scrap,
                        actualWidth,
                        0,
                        actualWidth + width,
                        height
                );
                actualWidth += width;
                //超出界面的就不画了,也不add了
                if (actualWidth > getWidth()) {
                    break;
                }
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return !vertical;
    }

    @Override
    public boolean canScrollVertically() {
        return vertical;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d("April", "getChildCount() " + getChildCount() + " recycler.getScrapList().size() " + recycler.getScrapList().size());

        //界面向下滚动的时候,dy为正,向上滚动的时候dy为负

        //填充
        fillHorizontal(dx, recycler);
        //滚动
        offsetChildrenHorizontal(dx * -1);

        //回收已经离开界面的
        recycleOutHorizontal(dx, recycler);
        return dx;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d("April", "getChildCount() " + getChildCount() + " recycler.getScrapList().size() " + recycler.getScrapList().size());

        //界面向下滚动的时候,dy为正,向上滚动的时候dy为负

        //填充
        fillVertical(dy, recycler);
        //滚动
        offsetChildrenVertical(dy * -1);

        //回收已经离开界面的
        recycleOutVertical(dy, recycler);

        return dy;
    }


    private void fillVertical(int dy, RecyclerView.Recycler recycler) {
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
            int layoutPosition = getPosition(firstView);

            if (firstView.getTop() >= 0) {
                View scrap;
                if (layoutPosition == 0) {
                    scrap = recycler.getViewForPosition(getItemCount() - 1);
                } else {
                    scrap = recycler.getViewForPosition(layoutPosition - 1);
                }
                addView(scrap, 0);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(scrap, 0, firstView.getTop() - height, width, firstView.getTop());
            }
        }
    }

    private void recycleOutVertical(int dy, RecyclerView.Recycler recycler) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == null) {
                continue;
            }
            if (dy > 0) {
                if (view.getBottom() - dy < 0) {
                    Log.d("April", "recycleOut " + i);
                    removeAndRecycleView(view, recycler);
                }
            } else {
                if (view.getTop() - dy > getHeight()) {
                    Log.d("April", "recycleOut " + i);
                    removeAndRecycleView(view, recycler);
                }
            }
        }
    }

    private void fillHorizontal(int dx, RecyclerView.Recycler recycler) {
        //向右滚动
        if (dx > 0) {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView == null) {
                return;
            }
            int lastPos = getPosition(lastView);
            if (lastView.getRight() - dx < getWidth()) {
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
                layoutDecorated(
                        scrap,
                        lastView.getRight(),
                        0,
                        lastView.getRight() + width,
                        height
                );
            }
        }
        //向左滚动
        else {
            View firstView = getChildAt(0);
            if (firstView == null) {
                return;
            }
            int layoutPosition = getPosition(firstView);

            if (firstView.getLeft() >= 0) {
                View scrap;
                if (layoutPosition == 0) {
                    scrap = recycler.getViewForPosition(getItemCount() - 1);
                } else {
                    scrap = recycler.getViewForPosition(layoutPosition - 1);
                }
                addView(scrap, 0);
                measureChildWithMargins(scrap, 0, 0);
                int width = getDecoratedMeasuredWidth(scrap);
                int height = getDecoratedMeasuredHeight(scrap);
                layoutDecorated(
                        scrap,
                        firstView.getLeft() - width,
                        0,
                        firstView.getLeft(),
                        height
                );
            }
        }
    }

    private void recycleOutHorizontal(int dx, RecyclerView.Recycler recycler) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == null) {
                continue;
            }
            //向右滚动
            if (dx > 0) {
                if (view.getRight() - dx < 0) {
                    Log.d("April", "recycleOut " + i);
                    removeAndRecycleView(view, recycler);
                }
            }
            //向左滚动
            else {
                if (view.getLeft() - dx > getWidth()) {
                    Log.d("April", "recycleOut " + i);
                    removeAndRecycleView(view, recycler);
                }
            }
        }
    }
}
