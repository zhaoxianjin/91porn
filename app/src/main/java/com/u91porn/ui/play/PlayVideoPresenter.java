package com.u91porn.ui.play;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Severity;
import com.google.gson.Gson;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import com.u91porn.MyApplication;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoComment;
import com.u91porn.data.model.VideoCommentResult;
import com.u91porn.data.model.VideoResult;
import com.u91porn.exception.VideoException;
import com.u91porn.ui.download.DownloadPresenter;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.CallBackWrapper;
import com.u91porn.utils.Keys;
import com.u91porn.utils.ParseUtils;
import com.u91porn.utils.RandomIPAdderssUtils;
import com.u91porn.utils.SPUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;

import io.objectbox.relation.RelationInfo;
import io.objectbox.relation.ToOne;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;
import okhttp3.Cookie;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class PlayVideoPresenter extends MvpBasePresenter<PlayVideoView> implements IPlay {

    private static final String TAG = PlayVideoPresenter.class.getSimpleName();
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi;
    private FavoritePresenter favoritePresenter;
    private DownloadPresenter downloadPresenter;
    private SharedPrefsCookiePersistor sharedPrefsCookiePersistor;
    private SetCookieCache setCookieCache;
    private CacheProviders cacheProviders;
    private LifecycleProvider<ActivityEvent> provider;
    private int commentPerPage = 20;
    private int start = 1;

    public PlayVideoPresenter(NoLimit91PornServiceApi mNoLimit91PornServiceApi, FavoritePresenter favoritePresenter, DownloadPresenter downloadPresenter, SharedPrefsCookiePersistor sharedPrefsCookiePersistor, SetCookieCache setCookieCache, CacheProviders cacheProviders, LifecycleProvider<ActivityEvent> provider) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
        this.favoritePresenter = favoritePresenter;
        this.downloadPresenter = downloadPresenter;
        this.sharedPrefsCookiePersistor = sharedPrefsCookiePersistor;
        this.setCookieCache = setCookieCache;
        this.cacheProviders = cacheProviders;
        this.provider = provider;
    }

    @Override
    public void loadVideoUrl(String viewKey, String referer) {

        String ip = RandomIPAdderssUtils.getRandomIPAdderss();
        cacheProviders.getVideoPlayPage(mNoLimit91PornServiceApi.getVideoPlayPage(viewKey, ip, referer), new DynamicKey(viewKey), new EvictDynamicKey(false))
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
                }).map(new Function<String, VideoResult>() {
            @Override
            public VideoResult apply(String s) throws VideoException {
                VideoResult videoResult = ParseUtils.parseVideoPlayUrl(s);
                if (TextUtils.isEmpty(videoResult.getVideoUrl())) {
                    if (videoResult.getId() == VideoResult.OUT_OF_WATCH_TIMES) {
                        //尝试强行重置，并上报异常
                        resetWatchTime(true);
                        Bugsnag.notify(new Throwable(TAG+":warning, ten videos each day"), Severity.WARNING);
                        throw new VideoException("观看次数达到上限了！");
                    } else {
                        throw new VideoException("解析视频链接失败了");
                    }
                }
                return videoResult;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<VideoResult>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new CallBackWrapper<VideoResult>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.showParsingDialog();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final VideoResult videoResult) {
                        resetWatchTime(false);
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.playVideo(videoResult);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.errorParseVideoUrl(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void loadVideoComment(String videoId, final boolean pullToRefresh, String referer) {
        if (pullToRefresh) {
            start = 1;
        }
        mNoLimit91PornServiceApi.getVideoComments(videoId, start, commentPerPage, referer)
                .map(new Function<String, List<VideoComment>>() {
                    @Override
                    public List<VideoComment> apply(String s) throws Exception {
                        return ParseUtils.parseVideoComment(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<List<VideoComment>>bindUntilEvent(ActivityEvent.STOP)).subscribe(new CallBackWrapper<List<VideoComment>>() {
            @Override
            public void onBegin(Disposable d) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        if (start == 1 && !pullToRefresh) {
                            view.showLoading(pullToRefresh);
                        }
                    }
                });
            }

            @Override
            public void onSuccess(final List<VideoComment> videoCommentList) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        if (start == 1) {
                            view.setVideoCommentData(videoCommentList, pullToRefresh);
                        } else {
                            view.setMoreVideoCommentData(videoCommentList);
                        }
                        if (videoCommentList.size() == 0 && start == 1) {
                            view.noMoreVideoCommentData("暂无评论");
                        } else if (videoCommentList.size() == 0 && start > 1) {
                            view.noMoreVideoCommentData("没有更多评论了");
                        }
                        start++;
                        view.showContent();
                    }
                });
            }

            @Override
            public void onError(final String msg, int code) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        if (start == 1) {
                            view.loadVideoCommentError(msg);
                        } else {
                            view.loadMoreVideoCommentError(msg);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void commentVideo(String comment, String uid, String vid, String referer) {
        String cpaintFunction = "process_comments";
        String responseType = "json";
        String comments = "\"" + comment + "\"";
        Logger.d(comments);
        mNoLimit91PornServiceApi.commentVideo(cpaintFunction, comments, uid, vid, responseType, referer)
                .map(new Function<String, VideoCommentResult>() {
                    @Override
                    public VideoCommentResult apply(String s) throws Exception {
                        return new Gson().fromJson(s, VideoCommentResult.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<VideoCommentResult>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new CallBackWrapper<VideoCommentResult>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final VideoCommentResult videoCommentResult) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                if (videoCommentResult.getA().size() == 0) {
                                    view.commentVideoError("评论错误，未知错误");
                                } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_SUCCESS) {
                                    view.commentVideoSuccess("留言已经提交，审核后通过");
                                } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_ALLREADY) {
                                    view.commentVideoError("你已经在这个视频下留言过.");
                                } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_NO_PERMISION) {
                                    view.commentVideoError("不允许留言!");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void replyComment(String comment, String username, String vid, String commentId, String referer) {
        mNoLimit91PornServiceApi.replyComment(comment, username, vid, commentId, referer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<String>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                if ("OK".equals(s)) {
                                    view.replyVideoCommentSuccess("留言已经提交，审核后通过");
                                } else {
                                    view.replyVideoCommentError("回复评论失败");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.showError(msg);
                            }
                        });
                    }
                })
        ;
    }

    /**
     * 重置观看次数
     *
     * @param forceReset 强行重置
     */
    private void resetWatchTime(final boolean forceReset) {
        List<Cookie> cookieList = sharedPrefsCookiePersistor.loadAll();
//        for (Cookie cookie : cookieList) {
//            if ("watch_times".equals(cookie.name())) {
//                if (TextUtils.isDigitsOnly(cookie.value())) {
//                    int watchTime = Integer.parseInt(cookie.value());
//                    if (watchTime >= 10) {
//                        Logger.t(TAG).d("已经观看10次，重置cookies");
//                        sharedPrefsCookiePersistor.delete(cookie);
//                        setCookieCache.delete(cookie);
//                    } else {
//                        Logger.t(TAG).d("当前已经看了：" + cookie.value() + " 次");
//                    }
//                } else if ("10".equals(cookie.value())) {
//                    Logger.t(TAG).d("已经观看10次，重置cookies");
//                    sharedPrefsCookiePersistor.delete(cookie);
//                    setCookieCache.delete(cookie);
//                } else {
//                    Logger.t(TAG).d("当前已经看了：" + cookie.value() + " 次");
//                }
//            }
//        }


        Observable
                .fromIterable(cookieList)
                .filter(new Predicate<Cookie>() {
                    @Override
                    public boolean test(Cookie cookie) throws Exception {
                        return "watch_times".equals(cookie.name());
                    }
                }).filter(new Predicate<Cookie>() {
            @Override
            public boolean test(Cookie cookie) throws Exception {
                boolean isDigitsOnly = TextUtils.isDigitsOnly(cookie.value());
                if (!isDigitsOnly) {
                    Logger.t(TAG).d("观看次数cookies异常");
                    Bugsnag.notify(new Throwable(TAG + ":cookie watchtimes is not DigitsOnly"), Severity.WARNING);
                }
                return isDigitsOnly;
            }
        }).filter(new Predicate<Cookie>() {
            @Override
            public boolean test(Cookie cookie) throws Exception {
                int watchTime = Integer.parseInt(cookie.value());
                Logger.t(TAG).d("当前已经看了：" + watchTime + " 次");
                if (forceReset) {
                    Logger.t(TAG).d("已经观看10次，重置cookies");
                    sharedPrefsCookiePersistor.delete(cookie);
                    setCookieCache.delete(cookie);
                }
                return watchTime >= 10;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<Cookie>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new CallBackWrapper<Cookie>() {
                    @Override
                    public void onBegin(Disposable d) {
                        Logger.t(TAG).d("开始读取观看次数");
                    }

                    @Override
                    public void onSuccess(Cookie cookie) {
                        Logger.t(TAG).d("已经观看10次，重置cookies");
                        sharedPrefsCookiePersistor.delete(cookie);
                        setCookieCache.delete(cookie);
                    }

                    @Override
                    public void onError(String msg, int code) {
                        Logger.t(TAG).d("重置观看次数出错了：" + msg);
                        Bugsnag.notify(new Throwable(TAG+":reset watchTimes error:" + msg), Severity.WARNING);
                    }
                });
    }

    @Override
    public void saveVideoUrl(VideoResult videoResult, UnLimit91PornItem unLimit91PornItem) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        Box<VideoResult> videoResultBox = MyApplication.getInstace().getBoxStore().boxFor(VideoResult.class);
        UnLimit91PornItem tmp = BoxQureyHelper.findByViewKey(unLimit91PornItem.getViewKey());
        if (tmp == null) {
            unLimit91PornItem.setFavorite(UnLimit91PornItem.FAVORITE_NO);
            unLimit91PornItem.videoResult.setTarget(videoResult);
            unLimit91PornItem.setViewHistoryDate(new Date());
            unLimit91PornItemBox.put(unLimit91PornItem);
        } else {
            videoResult.setId(tmp.getId());
            tmp.setViewHistoryDate(new Date());
            tmp.videoResult.setTarget(videoResult);
            unLimit91PornItemBox.put(tmp);
            videoResultBox.put(videoResult);
        }
    }

    @Override
    public void downloadVideo(UnLimit91PornItem unLimit91PornItem) {
        downloadPresenter.downloadVideo(unLimit91PornItem, new DownloadPresenter.DownloadListener() {
            @Override
            public void onSuccess(final String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.showMessage(message, TastyToast.SUCCESS);
                    }
                });
            }

            @Override
            public void onError(final String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.showMessage(message, TastyToast.ERROR);
                    }
                });
            }
        });
    }

    @Override
    public void favorite(String cpaintFunction, String uId, String videoId, String ownnerId, String responseType, String referer) {
        favoritePresenter.favorite(cpaintFunction, uId, videoId, ownnerId, responseType, referer, new FavoritePresenter.FavoriteListener() {
            @Override
            public void onSuccess(String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.favoriteSuccess();
                    }
                });
            }

            @Override
            public void onError(final String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.showError(message);
                    }
                });
            }
        });
    }
}
