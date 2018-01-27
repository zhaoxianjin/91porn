package com.u91porn.ui.notice;

import com.u91porn.data.model.Notice;
import com.u91porn.ui.update.UpdateView;

/**
 * @author flymegoc
 * @date 2018/1/26
 */

public interface NoticeView extends UpdateView {
    void haveNewNotice(Notice notice);

    void noNewNotice();

    void checkNewNoticeError(String message);
}
