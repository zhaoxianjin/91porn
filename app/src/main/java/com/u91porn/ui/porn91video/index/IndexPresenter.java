package com.u91porn.ui.porn91video.index;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.parser.Parse91PronVideo;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.utils.HeaderUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class IndexPresenter extends MvpBasePresenter<IndexView> implements IIndex {
    private static final String TAG = IndexPresenter.class.getSimpleName();
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi;
    private CacheProviders cacheProviders;
    private LifecycleProvider<Lifecycle.Event> provider;

    public IndexPresenter(NoLimit91PornServiceApi mNoLimit91PornServiceApi, CacheProviders cacheProviders, LifecycleProvider<Lifecycle.Event> provider) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
        this.cacheProviders = cacheProviders;
        this.provider = provider;
    }

    public void setNoLimit91PornServiceApi(NoLimit91PornServiceApi mNoLimit91PornServiceApi) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
    }

    /**
     * 加载首页视频数据
     *
     * @param pullToRefresh 是否刷新
     */
    @Override
    public void loadIndexData(final boolean pullToRefresh, boolean cleanCache) {
        Observable<String> indexPhpObservable = mNoLimit91PornServiceApi.indexPhp(HeaderUtils.getIndexHeader());
        cacheProviders.getIndexPhp(indexPhpObservable, new EvictProvider(cleanCache))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBodyReply) throws Exception {
                        switch (responseBodyReply.getSource()) {
                            case CLOUD:
                                Logger.t(TAG).d("数据来自：网络");
                                break;
                            case MEMORY:
                                Logger.t(TAG).d("数据来自：内存");
                                break;
                            case PERSISTENCE:
                                Logger.t(TAG).d("数据来自：磁盘缓存");
                                break;
                            default:
                                break;
                        }
                        return responseBodyReply.getData();
                    }

                })
                .map(new Function<String, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(String s) throws Exception {
                        return Parse91PronVideo.parseIndex(s);
                    }
                })
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<IndexView>() {
                            @Override
                            public void run(@NonNull IndexView view) {
                                if (!pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<IndexView>() {
                            @Override
                            public void run(@NonNull IndexView view) {
                                view.setData(unLimit91PornItems);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<IndexView>() {
                            @Override
                            public void run(@NonNull IndexView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
