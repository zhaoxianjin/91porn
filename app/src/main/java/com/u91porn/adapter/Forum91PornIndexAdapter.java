package com.u91porn.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.data.model.PinnedHeaderEntity;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/24
 */

public class Forum91PornIndexAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<Forum91PronItem>> {


    public Forum91PornIndexAdapter(List<PinnedHeaderEntity<Forum91PronItem>> data) {
        super(data);
    }

    @Override
    protected void addItemTypes() {
        addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_forum_91_porn_section_head);
        addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_forum_91_porn);
    }

    @Override
    protected void convert(BaseViewHolder helper, PinnedHeaderEntity<Forum91PronItem> item) {
        switch (helper.getItemViewType()) {
            case TYPE_HEADER:
                helper.setText(R.id.tv_item_forum_91_porn_section_header_title, item.getPinnedHeaderName());
                break;
            case TYPE_DATA:
                helper.setText(R.id.tv_item_forum_91_porn_title, item.getData().getTitle());
                helper.setText(R.id.tv_item_forum_91_porn_author_publish_time, item.getData().getAuthor() + "\n" + item.getData().getAuthorPublishTime());
                helper.setText(R.id.tv_item_forum_91_porn_reply_view, item.getData().getReplyCount() + "/" + item.getData().getViewCount());
                helper.setText(R.id.tv_item_forum_91_porn_last_post_author_time, item.getData().getLastPostAuthor() + "\n" + item.getData().getLastPostTime());
                break;
            default:
        }
    }
}
