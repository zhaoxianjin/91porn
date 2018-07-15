package com.u91porn.data;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public interface Mm99ServiceApi {
    @GET
    Observable<String> imageList(@Url String url);

    @Headers({"Referer: http://www.99mm.me/meitui/"})
    @GET("url.php")
    Observable<String> imageLists(@Query("act") String act,@Query("id") int id);
}
