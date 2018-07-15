package com.u91porn.ui.images.mm99;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.Api;
import com.u91porn.data.Mm99ServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.Mm99;
import com.u91porn.parser.Parse99Mm;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public class Mm99Presenter extends MvpBasePresenter<Mm99View> implements IMm99 {
    private CacheProviders cacheProviders;
    private Mm99ServiceApi mm99ServiceApi;
    private int page = 1;
    private int totalPage = 1;
    private LifecycleProvider<Lifecycle.Event> provider;

    public Mm99Presenter(CacheProviders cacheProviders, Mm99ServiceApi mm99ServiceApi, LifecycleProvider<Lifecycle.Event> provider) {
        this.cacheProviders = cacheProviders;
        this.mm99ServiceApi = mm99ServiceApi;
        this.provider = provider;
    }

    @Override
    public void loadData(String category, final boolean pullToRefresh, boolean cleanCache) {
        if (pullToRefresh) {
            page = 1;
            totalPage = 1;
        }
        String url = buildUrl(category, page);
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(category, page);
        EvictDynamicKeyGroup evictDynamicKeyGroup = new EvictDynamicKeyGroup(cleanCache);
        cacheProviders.cacheWithLimitTime(mm99ServiceApi.imageList(url), dynamicKeyGroup, evictDynamicKeyGroup)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<Mm99>>() {
                    @Override
                    public List<Mm99> apply(String s) throws Exception {
                        BaseResult<List<Mm99>> baseResult = Parse99Mm.parse99MmList(s, page);
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getData();
                    }
                })
                .compose(RxSchedulersHelper.<List<Mm99>>ioMainThread())
                .compose(provider.<List<Mm99>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<Mm99>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<Mm99View>() {
                            @Override
                            public void run(@NonNull Mm99View view) {
                                view.showLoading(pullToRefresh);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<Mm99> mm99s) {
                        ifViewAttached(new ViewAction<Mm99View>() {
                            @Override
                            public void run(@NonNull Mm99View view) {
                                if (page == 1) {
                                    view.setData(mm99s);
                                    view.showContent();
                                } else {
                                    view.setMoreData(mm99s);
                                }
                                //已经最后一页了
                                if (page >= totalPage) {
                                    view.noMoreData();
                                } else {
                                    page++;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<Mm99View>() {
                            @Override
                            public void run(@NonNull Mm99View view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    private String buildUrl(String category, int page) {
        switch (category) {
            case "meitui":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "meitui/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "meitui/mm_1_" + page + ".html";
                }

            case "xinggan":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "xinggan/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "xinggan/mm_2_" + page + ".html";
                }

            case "qingchun":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "qingchun/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "qingchun/mm_3_" + page + ".html";
                }

            case "hot":
                if (page == 1) {
                    return Api.APP_99_MM_DOMAIN + "hot/";
                } else {
                    return Api.APP_99_MM_DOMAIN + "hot/mm_4_" + page + ".html";
                }

            default:
                return Api.APP_99_MM_DOMAIN;
        }
    }
}
