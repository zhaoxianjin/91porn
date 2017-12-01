package com.u91porn.ui.common;

import android.text.TextUtils;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.MyApplication;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.CallBackWrapper;
import com.u91porn.utils.ParseUtils;

import java.util.List;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.Reply;
import io.rx_cache2.internal.RxCache;
import okhttp3.ResponseBody;

/**
 * @author flymegoc
 * @date 2017/11/16
 * @describe
 */

public class CommonPresenter extends MvpBasePresenter<CommonView> implements ICommon{
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
    private CacheProviders cacheProviders = MyApplication.getInstace().getCacheProviders();
    private String category;
    private String viewType = "basic";
    private Integer totalPage = 1;
    private int page = 1;
    private FavoritePresenter favoritePresenter;

    public CommonPresenter(String category) {
        this.category = category;
    }

    @Override
    public void loadHotData(final boolean pullToRefresh, String m) {

        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
        }
        //RxCache条件区别
        String condition;
        if (TextUtils.isEmpty(m)) {
            condition = category;
        } else {
            condition = category + m;
        }
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(condition, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(pullToRefresh);

        Observable<String> categoryPage = mNoLimit91PornServiceApi.getCategoryPage(category, viewType, page, m);
        cacheProviders.getCategoryPage(categoryPage, dynamicKeyGroup, evictDynamicKey)
                .compose(getView().bindView())
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
