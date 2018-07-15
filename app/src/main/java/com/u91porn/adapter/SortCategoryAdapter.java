package com.u91porn.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.Category;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/10/28
 * @describe
 */

public class SortCategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

    private OnStartDragListener onStartDragListener;

    public SortCategoryAdapter(int layoutResId, @Nullable List<Category> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Category category) {
        helper.setText(R.id.tv_sort_category_name, category.getCategoryName());
        SwitchCompat switchCompat = helper.getView(R.id.sw_sort_category);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                category.setIsShow(isChecked);
            }
        });
        switchCompat.setChecked(category.getIsShow());
        ImageView imageView = helper.getView(R.id.iv_drag_handle);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onStartDragListener != null) {
                    //注意：这里down和up都会回调该方法
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        onStartDragListener.startDragItem(helper);
                    }
                }
                return false;
            }
        });
    }

    public void setOnStartDragListener(OnStartDragListener onStartDragListener) {
        this.onStartDragListener = onStartDragListener;
    }

    public interface OnStartDragListener {
        /**
         * 触摸imageview，开启拖动的接口
         */
        void startDragItem(BaseViewHolder helper);
    }
}
