package com.u91porn.ui.pigav;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.PigAvServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.PigAv;
import com.u91porn.data.model.PigAvFormRequest;
import com.u91porn.data.model.PigAvLoadMoreResponse;
import com.u91porn.parser.ParsePigAv;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.MvpBasePresenter;
import com.u91porn.utils.AddressHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public class PigAvPresenter extends MvpBasePresenter<PigAvView> implements IPigAv {
    private static final String TAG = PigAvPresenter.class.getSimpleName();
    private PigAvServiceApi pigAvServiceApi;
    private int page = 2;
    private Gson gson;

    public PigAvPresenter(CacheProviders cacheProviders, LifecycleProvider<Lifecycle.Event> provider, PigAvServiceApi pigAvServiceApi) {
        super(cacheProviders, provider);
        this.pigAvServiceApi = pigAvServiceApi;
        gson = new Gson();
    }

    public void setPigAvServiceApi(PigAvServiceApi pigAvServiceApi) {
        this.pigAvServiceApi = pigAvServiceApi;
    }

    @Override
    public void videoList(String category, boolean pullToRefresh) {
        if (pullToRefresh) {
            page = 2;
        }
        DynamicKey dynamicKey = new DynamicKey(category);
        EvictDynamicKey evictDynamicKey = new EvictDynamicKey(pullToRefresh);
        if ("index".equals(category)) {
            action(cacheProviders.cacheWithLimitTime(pigAvServiceApi.videoList(AddressHelper.getInstance().getPigAvAddress()), dynamicKey, evictDynamicKey));
        } else {
            action(cacheProviders.cacheWithLimitTime(pigAvServiceApi.videoList(AddressHelper.getInstance().getPigAvAddress() + category + "av線上看"), dynamicKey, evictDynamicKey));
        }
    }

    private void action(Observable<Reply<String>> observable) {
        observable
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<PigAv>>() {
                    @Override
                    public List<PigAv> apply(String s) throws Exception {
                        BaseResult<List<PigAv>> baseResult = ParsePigAv.videoList(s);
                        return baseResult.getData();
                    }
                })
                .compose(RxSchedulersHelper.<List<PigAv>>ioMainThread())
                .compose(provider.<List<PigAv>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<PigAv>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<PigAv> pigAvs) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.setData(pigAvs);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void moreVideoList(String category, boolean pullToRefresh) {
        DynamicKeyGroup dynamicKeyGroup = new DynamicKeyGroup(category, page);
        EvictDynamicKeyGroup evictDynamicKeyGroup = new EvictDynamicKeyGroup(pullToRefresh);
        String action = "td_ajax_block";
        PigAvFormRequest pigAvFormRequest = new PigAvFormRequest();
        pigAvFormRequest.setLimit("10");
        pigAvFormRequest.setSort("random_posts");
        pigAvFormRequest.setAjax_pagination("load_more");
        pigAvFormRequest.setTd_column_number(3);
        pigAvFormRequest.setTd_filter_default_txt("所有");
        pigAvFormRequest.setClassX("td_uid_7_5a719c1244c2f_rand");
        pigAvFormRequest.setTdc_css_class("td_uid_7_5a719c1244c2f_rand");
        pigAvFormRequest.setTdc_css_class_style("td_uid_7_5a719c1244c2f_rand_style");
        String tdAtts = gson.toJson(pigAvFormRequest);
        String tdBlockId = "td_uid_7_5a719c1244c2f";
        int tdColumnNumber = 3;
        String blockType = "td_block_16";
        actionMore(cacheProviders.cacheWithLimitTime(pigAvServiceApi.moreVideoList(action, tdAtts, tdBlockId, tdColumnNumber, page, blockType, "", ""), dynamicKeyGroup, evictDynamicKeyGroup), pullToRefresh);
    }

    private void actionMore(Observable<Reply<String>> observable, final boolean pullToRefresh) {
        observable
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, List<PigAv>>() {
                    @Override
                    public List<PigAv> apply(String s) throws Exception {
                        PigAvLoadMoreResponse pigAvLoadMoreResponse = gson.fromJson(s, PigAvLoadMoreResponse.class);
                        BaseResult<List<PigAv>> baseResult = ParsePigAv.videoList(pigAvLoadMoreResponse.getTd_data());
                        return baseResult.getData();
                    }
                })
                .compose(RxSchedulersHelper.<List<PigAv>>ioMainThread())
                .compose(provider.<List<PigAv>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<PigAv>>() {
                    @Override
                    public void onSuccess(final List<PigAv> pigAvs) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                if (pigAvs.size() == 0) {
                                    Logger.t(TAG).d("没有数据哦");
                                } else {
                                    view.setMoreData(pigAvs);
                                    page++;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String msg, int code) {
                        ifViewAttached(new ViewAction<PigAvView>() {
                            @Override
                            public void run(@NonNull PigAvView view) {
                                view.loadMoreFailed();
                            }
                        });
                    }
                });
    }
}
