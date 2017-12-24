package com.u91porn.ui.recentupdates;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.utils.CallBackWrapper;
import com.u91porn.utils.ParseUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * 最近更新
 *
 * @author flymegoc
 * @date 2017/12/19
 */

public class RecentUpdatesPresenter extends MvpBasePresenter<RecentUpdatesView> implements IRencentUpdate {

    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private CacheProviders cacheProviders;
    private String next;
    private Integer totalPage = 1;
    private int page = 1;
    private LifecycleProvider<FragmentEvent> provider;
    /**
     * 本次强制刷新过那下面的请求也一起刷新
     */
    private boolean cleanCache = false;
    public RecentUpdatesPresenter(NoLimit91PornServiceApi noLimit91PornServiceApi, CacheProviders cacheProviders, String next, LifecycleProvider<FragmentEvent> provider) {
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
        this.cacheProviders = cacheProviders;
        this.next = next;
        this.provider = provider;
    }

    @Override
    public void loadRecentUpdatesData(final boolean pullToRefresh, String next) {
        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
            cleanCache=true;
        }
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(next, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache);

        Observable<String> categoryPage = noLimit91PornServiceApi.recentUpdates(next, page);
        cacheProviders.getRecentUpdates(categoryPage, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                })
                .map(new Function<String, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(String s) throws Exception {
                        BaseResult baseResult = ParseUtils.parseHot(s);
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getUnLimit91PornItemList();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(FragmentEvent.STOP))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        //首次加载显示加载页
                        if (isViewAttached() && page == 1 && !pullToRefresh) {
                            getView().showLoading(pullToRefresh);
                        }
                    }

                    @Override
                    public void onSuccess(List<UnLimit91PornItem> unLimit91PornItems) {
                        if (isViewAttached()) {
                            if (page == 1) {
                                getView().setData(unLimit91PornItems);
                                getView().showContent();
                            } else {
                                getView().loadMoreDataComplete();
                                getView().setMoreData(unLimit91PornItems);
                            }
                            //已经最后一页了
                            if (page == totalPage) {
                                getView().noMoreData();
                            } else {
                                page++;
                            }

                        }
                    }

                    @Override
                    public void onError(String msg, int code) {
                        //首次加载失败，显示重试页
                        if (isViewAttached() && page == 1) {
                            getView().showError(new Throwable(msg), false);
                            //否则就是加载更多失败
                        } else if (isViewAttached()) {
                            getView().loadMoreFailed();
                        }
                    }
                });
    }
}
