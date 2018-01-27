package com.u91porn.ui.viewimage;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.u91porn.data.MeiZiTuServiceApi;
import com.u91porn.data.model.BaseResult;
import com.u91porn.parse.ParseMeiZiTu;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2018/1/26
 */

public class PictureViewerPresenter extends MvpBasePresenter<PictureViewerView> implements IPictureViewer {

    private LifecycleProvider<ActivityEvent> provider;
    private MeiZiTuServiceApi meiZiTuServiceApi;

    public PictureViewerPresenter(LifecycleProvider<ActivityEvent> provider, MeiZiTuServiceApi meiZiTuServiceApi) {
        this.provider = provider;
        this.meiZiTuServiceApi = meiZiTuServiceApi;
    }

    @Override
    public void listMeZiPicture(int id) {
        meiZiTuServiceApi.imageList(id)
                .map(new Function<String, List<String>>() {
                    @Override
                    public List<String> apply(String s) throws Exception {
                        BaseResult<List<String>> baseResult = ParseMeiZiTu.parsePicturePage(s);
                        return baseResult.getData();
                    }
                })
                .compose(RxSchedulersHelper.<List<String>>ioMainThread())
                .compose(provider.<List<String>>bindUntilEvent(ActivityEvent.DESTROY))
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
