package com.u91porn.ui.images.viewimage;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.MeiZiTuServiceApi;
import com.u91porn.data.Mm99ServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.parser.ParseMeiZiTu;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2018/1/26
 */

public class PictureViewerPresenter extends MvpBasePresenter<PictureViewerView> implements IPictureViewer {

    private LifecycleProvider<Lifecycle.Event> provider;
    private MeiZiTuServiceApi meiZiTuServiceApi;
    private Mm99ServiceApi mm99ServiceApi;
    private CacheProviders cacheProviders;

    public PictureViewerPresenter(LifecycleProvider<Lifecycle.Event> provider, MeiZiTuServiceApi meiZiTuServiceApi, Mm99ServiceApi mm99ServiceApi, CacheProviders cacheProviders) {
        this.provider = provider;
        this.meiZiTuServiceApi = meiZiTuServiceApi;
        this.mm99ServiceApi = mm99ServiceApi;
        this.cacheProviders = cacheProviders;
    }

    @Override
    public void listMeZiPicture(int id, boolean pullToRefresh) {
        cacheProviders.meiZiTu(meiZiTuServiceApi.imageList(id), new DynamicKey(id), new EvictDynamicKey(pullToRefresh))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String s) throws Exception {
                        BaseResult<List<String>> baseResult = ParseMeiZiTu.parsePicturePage(s);
                        return baseResult.getData();
                    }
                })
                .compose(RxSchedulersHelper.<List<String>>ioMainThread())
                .compose(provider.<List<String>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<String>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PictureViewerView>() {
                            @Override
                            public void run(@NonNull PictureViewerView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<String> strings) {
                        ifViewAttached(new ViewAction<PictureViewerView>() {
                            @Override
                            public void run(@NonNull PictureViewerView view) {
                                view.setData(strings);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PictureViewerView>() {
                            @Override
                            public void run(@NonNull PictureViewerView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void list99MmPicture(final int id, final String imageUrl, boolean pullToRefresh) {
        cacheProviders.cacheWithNoLimitTime(mm99ServiceApi.imageLists("view", id), new DynamicKey(id), new EvictDynamicKey(pullToRefresh))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String s) throws Exception {
                        String[] tags = s.split(",");
                        List<String> stringList = new ArrayList<>();
                        for (int i = 0; i < tags.length; i++) {
                            stringList.add(imageUrl.replace("small/", "").replace(".jpg", "/" + (i + 1) + "-" + tags[i]) + ".jpg");
                        }
                        return stringList;
                    }
                })
                .compose(RxSchedulersHelper.<List<String>>ioMainThread())
                .compose(provider.<List<String>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<String>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PictureViewerView>() {
                            @Override
                            public void run(@NonNull PictureViewerView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<String> strings) {
                        ifViewAttached(new ViewAction<PictureViewerView>() {
                            @Override
                            public void run(@NonNull PictureViewerView view) {
                                view.setData(strings);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PictureViewerView>() {
                            @Override
                            public void run(@NonNull PictureViewerView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });

    }
}
