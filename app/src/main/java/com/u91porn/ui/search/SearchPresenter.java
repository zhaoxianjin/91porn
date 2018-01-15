package com.u91porn.ui.search;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.exception.VideoException;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.ParseUtils;
import com.u91porn.utils.RandomIPAdderssUtils;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2018/1/7
 */

public class SearchPresenter extends MvpBasePresenter<SearchView> implements ISearch {

    private static final String TAG = SearchPresenter.class.getSimpleName();
    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private LifecycleProvider<ActivityEvent> provider;

    private int page = 1;
    private Integer totalPage;

    public SearchPresenter(NoLimit91PornServiceApi noLimit91PornServiceApi, LifecycleProvider<ActivityEvent> provider) {
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
        this.provider = provider;
    }

    @Override
    public void searchVideos(String searchId, String sort, final boolean pullToRefresh) {
        String viewType = "basic";
        String searchType = "search_videos";
        if (pullToRefresh) {
            page = 1;
        }
        noLimit91PornServiceApi.search(viewType, page, searchType, searchId, sort, HeaderUtils.getIndexHeader(), RandomIPAdderssUtils.getRandomIPAdderss())
                .map(new Function<String, List<UnLimit91PornItem>>() {
                    @Override
                    public List<UnLimit91PornItem> apply(String s) throws Exception {
                        BaseResult baseResult = ParseUtils.parseSearchVideos(s);
                        if (baseResult.getCode() == BaseResult.ERROR_CODE) {
                            throw new VideoException(baseResult.getMessage());
                        }
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getUnLimit91PornItemList();
                    }
                })
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SearchView>() {
                            @Override
                            public void run(@NonNull SearchView view) {
                                if (page == 1 && pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItems) {
                        ifViewAttached(new ViewAction<SearchView>() {
                            @Override
                            public void run(@NonNull SearchView view) {
                                if (page == 1) {
                                    view.setData(unLimit91PornItems);
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
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        //首次加载失败，显示重试页
                        ifViewAttached(new ViewAction<SearchView>() {
                            @Override
                            public void run(@NonNull SearchView view) {
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
