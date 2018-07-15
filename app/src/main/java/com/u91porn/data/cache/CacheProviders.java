package com.u91porn.data.cache;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;
import io.rx_cache2.ProviderKey;
import io.rx_cache2.Reply;

/**
 * RxJavaCache 缓存
 *
 * @author flymegoc
 * @date 2017/11/18
 */

public interface CacheProviders {
    /**
     * 缓存自动过期时间15分钟
     */
    int CACHE_TIME = 15;

    /**
     * 缓存主页面
     *
     * @param indexPhp      主页oab
     * @param evictProvider 缓存控制
     * @return oab对象
     */
    @ProviderKey("indexPhp")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> getIndexPhp(Observable<String> indexPhp, EvictProvider evictProvider);

    /**
     * 缓存播放视频界面，无限制过期时间，除非手动清除
     *
     * @param playVideoPage 播放视频页oab
     * @param viewKey       观看的key
     * @param evictViewKey  缓存控制
     * @return oab对象
     */
    @ProviderKey("playVideo")
    Observable<Reply<String>> getVideoPlayPage(Observable<String> playVideoPage, DynamicKey viewKey, EvictDynamicKey evictViewKey);

    /**
     * 获取相应类别数据
     *
     * @param getCategoryPage    页码
     * @param filterPageCategory 类别
     * @param evictFilter        缓存控制
     * @return oab对象
     */
    @ProviderKey("categoryPage")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> getCategoryPage(Observable<String> getCategoryPage, DynamicKeyGroup filterPageCategory, EvictDynamicKey evictFilter);

    /**
     * 获取最近更新数据
     *
     * @param stringObservable   ob
     * @param filterPageCategory 页码
     * @param evictFilter        缓存控制
     * @return oab对象
     */
    @ProviderKey("recentUpdate")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> getRecentUpdates(Observable<String> stringObservable, DynamicKeyGroup filterPageCategory, EvictDynamicKey evictFilter);

    /**
     * 获取我的收藏
     *
     * @param stringObservable   ob
     * @param filterPageCategory 页码
     * @param evictFilter        缓存控制
     * @return oab对象
     */
    @ProviderKey("favorite")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> getFavorite(Observable<String> stringObservable, DynamicKeyGroup filterPageCategory, EvictDynamicKey evictFilter);

    /**
     * 作者视频
     *
     * @param stringObservable   ob
     * @param filterPageCategory 页码
     * @param evictFilter        缓存控制
     * @return ob
     */
    @ProviderKey("authorVideos")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> authorVideos(Observable<String> stringObservable, DynamicKeyGroup filterPageCategory, EvictDynamicKey evictFilter);

    /**
     * 妹子图分类
     *
     * @param observable           ob
     * @param dynamicKeyGroup      页码分类
     * @param evictDynamicKeyGroup 缓存控制
     * @return ob
     */
    @ProviderKey("mei_zi_tu")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> meiZiTu(Observable<String> observable, DynamicKeyGroup dynamicKeyGroup, EvictDynamicKeyGroup evictDynamicKeyGroup);

    /**
     * 妹子图浏览，永不过期
     *
     * @param observable ob
     * @return ob
     */
    @ProviderKey("mei_zi_tu_pic_list")
    Observable<Reply<String>> meiZiTu(Observable<String> observable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);



    @ProviderKey("cache_v113")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> cacheWithLimitTime(Observable<String> observable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    @ProviderKey("cache_v113")
    Observable<Reply<String>> cacheWithNoLimitTime(Observable<String> observable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    @ProviderKey("cache_v113")
    @LifeCache(duration = CACHE_TIME, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> cacheWithLimitTime(Observable<String> observable, DynamicKeyGroup dynamicKeyGroup, EvictDynamicKeyGroup evictDynamicKeyGroup);

    @ProviderKey("cache_v113")
    Observable<Reply<String>> cacheWithNoLimitTime(Observable<String> observable, DynamicKeyGroup dynamicKeyGroup, EvictDynamicKeyGroup evictDynamicKeyGroup);
}
