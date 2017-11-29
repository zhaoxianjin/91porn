package com.u91porn.ui.favorite;

import android.text.TextUtils;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.Constants;

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

/**
 * @author flymegoc
 * @date 2017/11/25
 * @describe
 */

public class FavoritePresenter extends MvpBasePresenter<FavoriteView> implements IFavorite {
    private Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);

    @Override
    public void favorite(UnLimit91PornItem unLimit91PornItem) {
        favorite(unLimit91PornItem, null);
    }

    public void favorite(UnLimit91PornItem unLimit91PornItem, FavoriteListener favoriteListener) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        UnLimit91PornItem tmp = BoxQureyHelper.findByViewKey(unLimit91PornItem.getViewKey());
        if (tmp == null) {
            unLimit91PornItem.setFavorite(UnLimit91PornItem.FAVORITE_YES);
            unLimit91PornItem.setFavoriteDate(new Date());
            unLimit91PornItemBox.put(unLimit91PornItem);
            if (isViewAttached() && favoriteListener == null) {
                getView().showMessage("收藏成功");
            } else {
                favoriteListener.onSuccess("收藏成功");
            }
        } else {
            if (tmp.getFavorite() == UnLimit91PornItem.FAVORITE_YES) {
                if (isViewAttached() && favoriteListener == null) {
                    getView().showMessage("已经收藏过了");
                } else {
                    favoriteListener.onError("已经收藏过了");
                }
            } else {
                tmp.setFavorite(UnLimit91PornItem.FAVORITE_YES);
                tmp.setFavoriteDate(new Date());
                unLimit91PornItemBox.put(tmp);
                if (isViewAttached() && favoriteListener == null) {
                    getView().showMessage("收藏成功");
                } else {
                    favoriteListener.onSuccess("收藏成功");
                }
            }
        }
    }

    @Override
    public void loadFavoriteData(int skip, int pageSize) {
        List<UnLimit91PornItem> unLimit91PornItemList = unLimit91PornItemBox.query().equal(UnLimit91PornItem_.favorite, UnLimit91PornItem.FAVORITE_YES).orderDesc(UnLimit91PornItem_.favoriteDate).build().find(skip, pageSize);
        if (isViewAttached()) {
            getView().setFavoriteData(unLimit91PornItemList);
            if (unLimit91PornItemList.size() == 0 || unLimit91PornItemList.size() < pageSize) {
                getView().noLoadMoreData();
            }
        }
    }

    @Override
    public void deleteFavorite(int position, UnLimit91PornItem unLimit91PornItem) {

        String videoUrl = BoxQureyHelper.getVideoUrlByViewKey(unLimit91PornItem.getViewKey());
        if (TextUtils.isEmpty(videoUrl)) {
            unLimit91PornItemBox.remove(unLimit91PornItem.getId());
        } else {
            unLimit91PornItem.setFavorite(UnLimit91PornItem.FAVORITE_NO);
            unLimit91PornItemBox.put(unLimit91PornItem);
        }
        if (isViewAttached()) {
            getView().deleteFavoriteSucc(position);
        }
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
                        CharSequence data = unLimit91PornItem.getVideoUrl() + "\r\n\r\n";
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        FileUtils.writeChars(file, "UTF-8", data, true);
                    }
                } else {
                    for (UnLimit91PornItem unLimit91PornItem : unLimit91PornItems) {
                        String title = unLimit91PornItem.getTitle();
                        String videoUrl = unLimit91PornItem.getVideoUrl();
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
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (isViewAttached()) {
                            getView().showMessage(s);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (isViewAttached()) {
                            getView().showMessage(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface FavoriteListener {
        void onSuccess(String message);

        void onError(String message);
    }
}
