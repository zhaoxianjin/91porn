package com.u91porn.ui.porn91video.author;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.exception.VideoException;
import com.u91porn.rxjava.CallBackWrapper;
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
 * @date 2018/1/8
 */

public class AuthorPresenter extends MvpBasePresenter<AuthorView> implements IAuthor {
    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private LifecycleProvider<Lifecycle.Event> provider;
    private CacheProviders cacheProviders;
    private int page = 1;
    private Integer totalPage;
    private boolean cleanCache;

    public AuthorPresenter(NoLimit91PornServiceApi noLimit91PornServiceApi, LifecycleProvider<Lifecycle.Event> provider, CacheProviders cacheProviders) {
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
        this.provider = provider;
        this.cacheProviders = cacheProviders;
    }

    @Override
    public void authorVideos(String uid, final boolean pullToRefresh) {
        String type = "public";
        if (pullToRefresh) {
            page = 1;
            cleanCache = true;
        }
        //RxCache条件区别
        String condition = null;
        if (!TextUtils.isEmpty(uid)) {
            condition = uid;
        }
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(condition, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache);

        Observable<String> stringObservable = noLimit91PornServiceApi.authorVideos(uid, type, page);
        cacheProviders.authorVideos(stringObservable, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                })
                .map(new Function<String, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(String s) throws Exception {
                        BaseResult<List<UnLimit91PornItem>> baseResult = Parse91PronVideo.parseAuthorVideos(s);
                        if (baseResult.getCode() == BaseResult.ERROR_CODE) {
                            throw new VideoException(baseResult.getMessage());
                        }
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
                        ifViewAttached(new ViewAction<AuthorView>() {
                            @Override
                            public void run(@NonNull AuthorView view) {
                                if (page == 1 && !pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<AuthorView>() {
                            @Override
                            public void run(@NonNull AuthorView view) {
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
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        //首次加载失败，显示重试页
                        ifViewAttached(new ViewAction<AuthorView>() {
                            @Override
                            public void run(@NonNull AuthorView view) {
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
