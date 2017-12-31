package com.u91porn.ui.favorite;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import com.u91porn.MyApplication;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.Favorite;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;
import com.u91porn.data.model.User;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.CallBackWrapper;
import com.u91porn.utils.Constants;
import com.u91porn.utils.ParseUtils;

import org.greenrobot.essentials.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2017/11/25
 * @describe
 */

public class FavoritePresenter extends MvpBasePresenter<FavoriteView> implements IFavorite {
    private static final String TAG = FavoriteListener.class.getSimpleName();
    private Box<UnLimit91PornItem> unLimit91PornItemBox;
    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private CacheProviders cacheProviders;
    private User user;
    private Integer totalPage = 1;
    private int page = 1;
    private LifecycleProvider<ActivityEvent> provider;
    /**
     * 本次强制刷新过那下面的请求也一起刷新
     */
    private boolean cleanCache = false;

    public FavoritePresenter(Box<UnLimit91PornItem> unLimit91PornItemBox, NoLimit91PornServiceApi noLimit91PornServiceApi, CacheProviders cacheProviders, User user, LifecycleProvider<ActivityEvent> provider) {
        this.unLimit91PornItemBox = unLimit91PornItemBox;
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
        this.cacheProviders = cacheProviders;
        this.user = user;
        this.provider = provider;
    }

    @Override
    public void favorite(String cpaintFunction, String uId, String videoId, String ownnerId, String responseType) {
        favorite(cpaintFunction, uId, videoId, ownnerId, responseType, null);
    }

    public void favorite(String cpaintFunction, String uId, String videoId, String ownnerId, String responseType, final FavoriteListener favoriteListener) {
        noLimit91PornServiceApi.favoriteVideo(cpaintFunction, uId, videoId, ownnerId, responseType)
                .map(new Function<String, Favorite>() {
                    @Override
                    public Favorite apply(String s) throws Exception {
                        return new Gson().fromJson(s, Favorite.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<Favorite>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new CallBackWrapper<Favorite>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final Favorite favorite) {
                        Logger.t(TAG).d(favorite);
                        if (favoriteListener != null) {
                            if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_SUCCESS) {
                                favoriteListener.onSuccess("收藏成功");
                            } else if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_FAIL) {
                                favoriteListener.onError("收藏失败");
                            } else if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_ALREADY) {
                                favoriteListener.onError("已经收藏过了");
                            } else if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_YOURSELF) {
                                favoriteListener.onError("不能收藏自己的视频");
                            }

                        } else {
                            ifViewAttached(new ViewAction<FavoriteView>() {
                                @Override
                                public void run(@NonNull FavoriteView view) {
                                    if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_SUCCESS) {
                                        view.showMessage("收藏成功", TastyToast.SUCCESS);
                                    } else if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_FAIL) {
                                        view.showMessage("收藏失败", TastyToast.ERROR);
                                    } else if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_ALREADY) {
                                        view.showMessage("已经收藏过了", TastyToast.INFO);
                                    } else if (favorite.getAddFavMessage().get(0).getData() == Favorite.FAVORITE_YOURSELF) {
                                        view.showMessage("不能收藏自己的视频", TastyToast.WARNING);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (favoriteListener != null) {
                            favoriteListener.onError(msg);
                        } else {
                            ifViewAttached(new ViewAction<FavoriteView>() {
                                @Override
                                public void run(@NonNull FavoriteView view) {
                                    view.showMessage(msg, TastyToast.ERROR);
                                }
                            });
                        }
                    }
                });
    }


    @Override
    public void loadRemoteFavoriteData(final boolean pullToRefresh) {
        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
            cleanCache = true;
        }
        //RxCache条件区别
        String condition = null;
        if (user != null) {
            condition = user.getUserName();
        }
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(condition, page);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(cleanCache);

        Observable<String> favoriteObservable = noLimit91PornServiceApi.myFavorite(page);

        cacheProviders.getFavorite(favoriteObservable, dynamicKeyGroup, evictDynamicKey)
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBody) throws Exception {
                        return responseBody.getData();
                    }
                })
                .map(new Function<String, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(String s) throws Exception {
                        BaseResult baseResult = ParseUtils.parseMyFavorite(s);
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getUnLimit91PornItemList();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        //首次加载显示加载页
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                if (page == 1 && !pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                if (page == 1) {
                                    view.setFavoriteData(unLimit91PornItems);
                                    view.showContent();
                                } else {
                                    view.loadMoreDataComplete();
                                    view.setMoreData(unLimit91PornItems);
                                }
                                //已经最后一页了
                                if (page == totalPage) {
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
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
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

    @Override
    public void deleteFavorite(final int position, UnLimit91PornItem unLimit91PornItem) {

        String videoUrl = BoxQureyHelper.getVideoUrlByViewKey(unLimit91PornItem.getViewKey());
        if (TextUtils.isEmpty(videoUrl)) {
            unLimit91PornItemBox.remove(unLimit91PornItem.getId());
        } else {
            unLimit91PornItem.setFavorite(UnLimit91PornItem.FAVORITE_NO);
            unLimit91PornItemBox.put(unLimit91PornItem);
        }
        ifViewAttached(new ViewAction<FavoriteView>() {
            @Override
            public void run(@NonNull FavoriteView view) {
                view.deleteFavoriteSucc(position);
            }
        });
    }

    @Override
    public void exportData(final boolean onlyUrl) {
        Observable.create(new ObservableOnSubscribe<List<UnLimit91PornItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UnLimit91PornItem>> e) throws Exception {
                List<UnLimit91PornItem> unLimit91PornItems = unLimit91PornItemBox.query().equal(UnLimit91PornItem_.favorite, UnLimit91PornItem.FAVORITE_YES).orderDesc(UnLimit91PornItem_.favoriteDate).build().find();
                e.onNext(unLimit91PornItems);
                e.onComplete();
            }
        }).map(new Function<List<UnLimit91PornItem>, String>() {
            @Override
            public String apply(List<UnLimit91PornItem> unLimit91PornItems) throws Exception {
                File file = new File(Constants.EXPORT_FILE);
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new Exception("导出失败,因为删除原文件失败了");
                    }

                }
                if (!file.createNewFile()) {
                    throw new Exception("导出失败,创建新文件失败了");
                }
                if (onlyUrl) {
                    for (UnLimit91PornItem unLimit91PornItem : unLimit91PornItems) {
                        CharSequence data = unLimit91PornItem.getVideoResult().getTarget().getVideoUrl() + "\r\n\r\n";
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        FileUtils.writeChars(file, "UTF-8", data, true);
                    }
                } else {
                    for (UnLimit91PornItem unLimit91PornItem : unLimit91PornItems) {
                        String title = unLimit91PornItem.getTitle();
                        String videoUrl = unLimit91PornItem.getVideoResult().getTarget().getVideoUrl();
                        CharSequence data = title + "\r\n" + videoUrl + "\r\n\r\n";
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        FileUtils.writeChars(file, "UTF-8", data, true);
                    }
                }
                return "导出成功";
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<String>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                view.showMessage(s, TastyToast.SUCCESS);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<FavoriteView>() {
                            @Override
                            public void run(@NonNull FavoriteView view) {
                                view.showMessage(msg, TastyToast.ERROR);
                            }
                        });
                    }
                });
    }

    public interface FavoriteListener {
        void onSuccess(String message);

        void onError(String message);
    }
}
