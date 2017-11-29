package com.u91porn.data;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @author flymegoc
 * @date 2017/11/14
 * @describe
 */

public interface NoLimit91PornServiceApi {
    /**
     * 91主页index.php
     *
     * @return body
     */
    @GET("/index.php")
    Observable<String> indexPhp();

    /**
     * 访问页面获取视频地址页面
     *
     * @param viewkey   视频的key
     * @param ipAddress 随机访问地址，为了突破限制游客每天10次观看次数
     * @return body
     */
    @GET("/view_video.php")
    Observable<String> getVideoPlayPage(@Query("viewkey") String viewkey, @Header("X-Forwarded-For") String ipAddress);

    /**
     * 获取相应类别数据
     *
     * @param category 类别
     * @param viewtype 类型
     * @param m        标记上下月，上月为 -1，其他直接null即可
     * @return body
     */
    @GET("/v.php")
    Observable<String> getCategoryPage(@Query("category") String category, @Query("viewtype") String viewtype, @Query("page") Integer page, @Query("m") String m);
}
