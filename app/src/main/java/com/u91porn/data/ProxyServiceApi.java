package com.u91porn.data;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/1/20
 */

public interface ProxyServiceApi {
    @GET
    Observable<String> parseGouBanJia(@Url String url);
}
