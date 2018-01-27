package com.u91porn.data;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/1/17
 */

public interface MeiZiTuServiceApi {

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN,})
    @GET("page/{page}/")
    Observable<String> index(@Path("page") int page);

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN})
    @GET("hot/page/{page}/")
    Observable<String> hot(@Path("page") int page);

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN})
    @GET("best/page/{page}/")
    Observable<String> best(@Path("page") int page);

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN})
    @GET("xinggan/page/{page}/")
    Observable<String> sexy(@Path("page") int page);

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN})
    @GET("japan/page/{page}/")
    Observable<String> japan(@Path("page") int page);

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN})
    @GET("taiwan/page/{page}/")
    Observable<String> taiwan(@Path("page") int page);

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN})
    @GET("mm/page/{page}/")
    Observable<String> mm(@Path("page") int page);

    @Headers({"Referer: " + Api.APP_MEIZITU_DOMAIN})
    @GET("{id}")
    Observable<String> imageList(@Path("id") int id);
}
