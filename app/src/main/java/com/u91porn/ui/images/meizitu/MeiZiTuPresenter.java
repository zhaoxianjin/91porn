package com.u91porn.ui.images.meizitu;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.MeiZiTuServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.MeiZiTu;
import com.u91porn.parser.ParseMeiZiTu;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2018/1/25
 */

public class MeiZiTuPresenter extends MvpBasePresenter<MeiZiTuView> implements IMeiZiTu {

    private MeiZiTuServiceApi meiZiTuServiceApi;
    protected LifecycleProvider<Lifecycle.Event> provider;
    private CacheProviders cacheProviders;
    private int page = 1;
    private int totalPage = 1;

    public MeiZiTuPresenter(MeiZiTuServiceApi meiZiTuServiceApi, LifecycleProvider<Lifecycle.Event> provider, CacheProviders cacheProviders) {
        this.meiZiTuServiceApi = meiZiTuServiceApi;
        this.provider = provider;
        this.cacheProviders = cacheProviders;
    }

    @Override
    public void listMeiZi(String tag, boolean pullToRefresh) {
        if (pullToRefresh) {
            page = 1;
        }
        switch (tag) {
            case "index":
                action(meiZiTuServiceApi.index(page), tag, pullToRefresh);
                break;
            case "hot":
                action(meiZiTuServiceApi.hot(page), tag, pullToRefresh);
                break;
            case "best":
                action(meiZiTuServiceApi.best(page), tag, pullToRefresh);
                break;
            case "japan":
                action(meiZiTuServiceApi.japan(page), tag, pullToRefresh);
                break;
            case "taiwan":
                action(meiZiTuServiceApi.taiwan(page), tag, pullToRefresh);
                break;
            case "xinggan":
                action(meiZiTuServiceApi.sexy(page), tag, pullToRefresh);
                break;
            case "mm":
                action(meiZiTuServiceApi.mm(page), tag, pullToRefresh);
                break;
            default:
        }
    }

    private void action(Observable<String> stringObservable, String tag, final boolean pullToRefresh) {

        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(tag, page);
        EvictDynamicKeyGroup evictDynamicKeyGroup = new EvictDynamicKeyGroup(pullToRefresh);

        cacheProviders.meiZiTu(stringObservable, dynamicKeyGroup, evictDynamicKeyGroup)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<MeiZiTu>>() {
                    @Override
                    public List<MeiZiTu> apply(String s) throws Exception {
                        BaseResult<List<MeiZiTu>> baseResult = ParseMeiZiTu.parseMeiZiTuList(s, page);
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getData();
                    }
                })
                .compose(RxSchedulersHelper.<List<MeiZiTu>>ioMainThread())
                .compose(provider.<List<MeiZiTu>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<MeiZiTu>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<MeiZiTuView>() {
                            @Override
                            public void run(@NonNull MeiZiTuView view) {
                                view.showLoading(pullToRefresh);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<MeiZiTu> meiZiTus) {
                        ifViewAttached(new ViewAction<MeiZiTuView>() {
                            @Override
                            public void run(@NonNull MeiZiTuView view) {
                                if (page == 1) {
                                    view.setData(meiZiTus);
                                    view.showContent();
                                } else {
                                    view.setMoreData(meiZiTus);
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
                        ifViewAttached(new ViewAction<MeiZiTuView>() {
                            @Override
                            public void run(@NonNull MeiZiTuView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
