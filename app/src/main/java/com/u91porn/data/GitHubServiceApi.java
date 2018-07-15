package com.u91porn.data;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/1/17
 */

public interface GitHubServiceApi {
    /**
     * 检查更新
     *
     * @param url 链接
     * @return ob
     */
    @GET
    Observable<String> checkUpdate(@Url String url);

    /**
     * 检查新公告
     *
     * @param url 链接
     * @return ob
     */
    @GET
    Observable<String> checkNewNotice(@Url String url);
}
