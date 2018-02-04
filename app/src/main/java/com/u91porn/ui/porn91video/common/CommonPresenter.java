package com.u91porn.ui.porn91video.common;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.parser.Parse91PronVideo;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2017/11/16
 */

public class CommonPresenter extends MvpBasePresenter<CommonView> implements ICommon {
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi;
    private CacheProviders cacheProviders;
    private Integer totalPage = 1;
    private int page = 1;
    private LifecycleProvider<Lifecycle.Event> provider;
    /**
     * 本次强制刷新过那下面的请求也一起刷新
     */
    private boolean isLoadMoreCleanCache = false;

    public CommonPresenter(NoLimit91PornServiceApi mNoLimit91PornServiceApi, CacheProviders cacheProviders, LifecycleProvider<Lifecycle.Event> provider) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
        this.cacheProviders = cacheProviders;
        this.provider = provider;
    }

    public void setNoLimit91PornServiceApi(NoLimit91PornServiceApi mNoLimit91PornServiceApi) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
    }

    @Override
    public void loadHotData(final boolean pullToRefresh, boolean cleanCache, String category, String m) {
        String viewType = "basic";
        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
            isLoadMoreCleanCache = true;
        }
        //RxCache条件区别
        String condition;
        if (TextUtils.isEmpty(m)) {
            condition = category;
        } else {
            condition = category + m;
        }
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(condition, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache || isLoadMoreCleanCache);

        Observable<String> categoryPage = mNoLimit91PornServiceApi.getCategoryPage(category, viewType, page, m, HeaderUtils.getIndexHeader());
        cacheProviders.getCategoryPage(categoryPage, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                })
                .map(new Function<String, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(String s) throws Exception {
                        BaseResult<List<UnLimit91PornItem>> baseResult = Parse91PronVideo.parseHot(s);
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getData();
                    }
                })
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        //首次加载显示加载页
                        ifViewAttached(new ViewAction<CommonView>() {
                            @Override
                            public void run(@NonNull CommonView view) {
                                if (page == 1 && !pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<CommonView>() {
                            @Override
                            public void run(@NonNull CommonView view) {
                                if (page == 1) {
                                    view.setData(unLimit91PornItems);
                                    view.showContent();
                                } else {
                                    view.loadMoreDataComplete();
                                    view.setMoreData(unLimit91PornItems);
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
                        //首次加载失败，显示重试页
                        ifViewAttached(new ViewAction<CommonView>() {
                            @Override
                            public void run(@NonNull CommonView view) {
                                if (page == 1) {
                                    view.showError(msg);
                                } else {
                                    view.loadMoreFailed();
                                }
                            }
                        });
                    }
                });
    }
}
