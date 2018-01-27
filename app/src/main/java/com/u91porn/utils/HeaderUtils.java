package com.u91porn.utils;

import com.u91porn.MyApplication;

/**给每个请求添加对应的referer header，一定程度上能改善超时现象
 * @author flymegoc
 * @date 2018/1/2
 */

public class HeaderUtils {
    /**
     * 来自播放列表的header
     *
     * @param viewKey 视频key
     * @return header
     */
    public static String getPlayVideoReferer(String viewKey) {
        return AddressHelper.getInstance().getVideo91PornAddress() + "view_video.php?viewkey=" + viewKey;
    }

    /**
     * 来自主页的header
     *
     * @return header
     */
    public static String getIndexHeader() {
        return AddressHelper.getInstance().getVideo91PornAddress() + "index.php";
    }

    /**
     * 收藏
     * @return header
     */
    public static String getFavHeader(){
        return AddressHelper.getInstance().getVideo91PornAddress() + "my_favour.php";
    }

    /**
     * 获取用户header
     * @param action login or register
     * @return header
     */
    public static String getUserHeader(String action) {
        return AddressHelper.getInstance().getVideo91PornAddress() + action + ".php";
    }
}
