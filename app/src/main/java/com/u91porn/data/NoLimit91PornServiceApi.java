package com.u91porn.data;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    /**
     * 最近更新
     *
     * @param next 参数
     * @return ob
     */
    @GET("/v.php")
    Observable<String> recentUpdates(@Query("next") String next, @Query("page") Integer page);

    /**
     * @param username     用户名
     * @param password     密码
     * @param fingerprint  人机识别码
     * @param fingerprint2 人机识别码
     * @param captcha      验证码
     * @param actionlogin  登录
     * @param x            x坐标
     * @param y            y坐标
     * @return ob
     */
    @FormUrlEncoded
    @POST("/login.php")
    Observable<String> login(@Field("username") String username, @Field("password") String password, @Field("fingerprint") String fingerprint, @Field("fingerprint2") String fingerprint2, @Field("captcha_input") String captcha, @Field("action_login") String actionlogin, @Field("x") String x, @Field("y") String y);

    /**
     * 我的收藏
     *
     * @return ob
     */
    @GET("/my_favour.php")
    Observable<String> myFavorite(@Query("page") int page);

    /**
     * 收藏视频
     *
     * @param cpaintFunction 动作
     * @param uId            用户id
     * @param videoId        视频id
     * @param ownerId        视频发布者id
     * @param responseType   返回类型
     * @return ob
     */
    @GET("/ajax/myajaxphp.php")
    Observable<String> favoriteVideo(@Query("cpaint_function") String cpaintFunction, @Query("cpaint_argument[]") String uId, @Query("cpaint_argument[]") String videoId, @Query("cpaint_argument[]") String ownerId, @Query("cpaint_response_type") String responseType);

    @GET
    Observable<String> checkUpdate(@Url String url);
}
