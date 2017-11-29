package com.u91porn.ui.index;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.utils.ParseUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class IndexPresenter extends MvpBasePresenter<IndexView> implements IIndex {
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
    private CacheProviders cacheProviders = MyApplication.getInstace().getCacheProviders();
    private FavoritePresenter favoritePresenter;

    /**
     * 加载首页视频数据
     *
     * @param pullToRefresh 是否刷新
     */
    @Override
    public void loadIndexData(final boolean pullToRefresh) {
        Observable<String> indexPhpObservable = mNoLimit91PornServiceApi.indexPhp();
        cacheProviders.getIndexPhp(indexPhpObservable, new EvictProvider(pullToRefresh))
                .compose(getView().bindView())
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBodyReply) throws Exception {
                        switch (responseBodyReply.getSource()) {
                            case CLOUD:
                                Logger.d("数据来自：网络");
                                break;
                            case MEMORY:
                                Logger.d("数据来自：内存");
                                break;
                            case PERSISTENCE:
                                Logger.d("数据来自：磁盘缓存");
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
                        return ParseUtils.parseIndex(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<UnLimit91PornItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (isViewAttached() && !pullToRefresh) {
                            getView().showLoading(pullToRefresh);
                        }
                    }

                    @Override
                    public void onNext(List<UnLimit91PornItem> itemList) {
                        if (isViewAttached()) {
                            getView().setData(itemList);
                            getView().showContent();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached()) {
                            getView().showError(e, false);
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().showContent();
                    }
                });
    }

    @Override
    public void favorite(UnLimit91PornItem unLimit91PornItem) {
        if (favoritePresenter == null) {
            favoritePresenter = new FavoritePresenter();
        }
        favoritePresenter.favorite(unLimit91PornItem, new FavoritePresenter.FavoriteListener() {
            @Override
            public void onSuccess(String message) {
                if (isViewAttached()) {
                    getView().showMessage(message);
                }
            }

            @Override
            public void onError(String message) {
                if (isViewAttached()) {
                    getView().showMessage(message);
                }
            }
        });
    }
}
