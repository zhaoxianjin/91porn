package com.u91porn.data;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @author flymegoc
 * @date 2018/1/21
 */

public interface Forum91PronServiceApi {

    @GET("index.php")
    Observable<String> index();

    /**
     * 加载板块列表
     *
     * @param fid  板块id
     * @param page 页码
     * @return ob
     */
    @GET("forumdisplay.php")
    Observable<String> forumdisplay(@Query("fid") String fid, @Query("page") int page);

    @Headers({
            "Referer: http://93.t9p.today/index.php",
            "Host: 93.t9p.today"
    })
    @GET("viewthread.php")
    Observable<String> forumItemContent(@Query("tid") Long tid);
}
