package com.u91porn.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.ProxyModel;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/20
 */

public class ProxyAdapter extends BaseQuickAdapter<ProxyModel, BaseViewHolder> {
    private int clickPosition = -1;

    public ProxyAdapter(int layoutResId, @Nullable List<ProxyModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProxyModel item) {
        helper.setText(R.id.tv_item_proxy_ip_address, item.getProxyIp());
        helper.setText(R.id.tv_item_proxy_port, item.getProxyPort());
        helper.setText(R.id.tv_item_proxy_anonymous, item.getAnonymous());
        helper.setText(R.id.tv_item_proxy_delay_time, item.getResponseTime());
        if (helper.getLayoutPosition() == clickPosition) {
            helper.itemView.setBackgroundColor(ContextCompat.getColor(helper.itemView.getContext(), R.color.colorPrimary));
        } else {
            helper.itemView.setBackgroundColor(ContextCompat.getColor(helper.itemView.getContext(), R.color.common_background));
        }
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition + 1;
        notifyDataSetChanged();
    }
}
