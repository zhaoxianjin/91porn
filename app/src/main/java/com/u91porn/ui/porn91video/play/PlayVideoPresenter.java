package com.u91porn.ui.porn91video.play;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Severity;
import com.google.gson.Gson;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoComment;
import com.u91porn.data.model.VideoCommentResult;
import com.u91porn.data.model.VideoResult;
import com.u91porn.exception.VideoException;
import com.u91porn.parser.Parse91PronVideo;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.download.DownloadPresenter;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.HeaderUtils;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
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
    private LifecycleProvider<Lifecycle.Event> provider;
    private int commentPerPage = 20;
    private int start = 1;
    private DataBaseManager dataBaseManager;

    public PlayVideoPresenter(NoLimit91PornServiceApi mNoLimit91PornServiceApi, FavoritePresenter favoritePresenter, DownloadPresenter downloadPresenter, SharedPrefsCookiePersistor sharedPrefsCookiePersistor, SetCookieCache setCookieCache, CacheProviders cacheProviders, LifecycleProvider<Lifecycle.Event> provider, DataBaseManager dataBaseManager) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
        this.favoritePresenter = favoritePresenter;
        this.downloadPresenter = downloadPresenter;
        this.sharedPrefsCookiePersistor = sharedPrefsCookiePersistor;
        this.setCookieCache = setCookieCache;
        this.cacheProviders = cacheProviders;
        this.provider = provider;
        this.dataBaseManager = dataBaseManager;
    }

    public void setNoLimit91PornServiceApi(NoLimit91PornServiceApi mNoLimit91PornServiceApi) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
    }

    @Override
    public void loadVideoUrl(final UnLimit91PornItem unLimit91PornItem) {
        String viewKey = unLimit91PornItem.getViewKey();
        String ip = AddressHelper.getRandomIPAddress();
        cacheProviders.getVideoPlayPage(mNoLimit91PornServiceApi.getVideoPlayPage(viewKey, ip, HeaderUtils.getIndexHeader()), new DynamicKey(viewKey), new EvictDynamicKey(false))
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
                .map(new Function<String, VideoResult>() {
                    @Override
                    public VideoResult apply(String s) throws VideoException {
                        VideoResult videoResult = Parse91PronVideo.parseVideoPlayUrl(s);
                        if (TextUtils.isEmpty(videoResult.getVideoUrl())) {
                            if (VideoResult.OUT_OF_WATCH_TIMES.equals(videoResult.getId())) {
                                //尝试强行重置，并上报异常
                                resetWatchTime(true);
                                Bugsnag.notify(new Throwable(TAG + ":ten videos each day host:" + AddressHelper.getInstance().getVideo91PornAddress()), Severity.WARNING);
                                throw new VideoException("观看次数达到上限了！");
                            } else {
                                throw new VideoException("解析视频链接失败了");
                            }
                        }
                        return videoResult;
                    }
                })
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<VideoResult>ioMainThread())
                .compose(provider.<VideoResult>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
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
                                view.playVideo(saveVideoUrl(videoResult, unLimit91PornItem));
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
                        return Parse91PronVideo.parseVideoComment(s);
                    }
                })
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<List<VideoComment>>ioMainThread())
                .compose(provider.<List<VideoComment>>bindUntilEvent(Lifecycle.Event.ON_STOP))
                .subscribe(new CallBackWrapper<List<VideoComment>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                if (start == 1 && pullToRefresh) {
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

                    @Override
                    public void onCancel(boolean isCancel) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                Logger.t(TAG).d("------getVideoComments  onCancel----------------------------");
                                if (start == 1) {
                                    view.loadVideoCommentError("取消请求");
                                } else {
                                    view.loadMoreVideoCommentError("取消请求");
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
                .map(new Function<VideoCommentResult, String>() {
                    @Override
                    public String apply(VideoCommentResult videoCommentResult) throws Exception {
                        String msg = "评论错误，未知错误";
                        if (videoCommentResult.getA().size() == 0) {
                            throw new VideoException("评论错误，未知错误");
                        } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_SUCCESS) {
                            msg = "留言已经提交，审核后通过";
                        } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_ALLREADY) {
                            throw new VideoException("你已经在这个视频下留言过");
                        } else if (videoCommentResult.getA().get(0).getData() == VideoCommentResult.COMMENT_NO_PERMISION) {
                            throw new VideoException("不允许留言!");
                        }
                        return msg;
                    }
                })
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String result) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.commentVideoSuccess(result);
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
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
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
                .compose(provider.<Cookie>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
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
                        Bugsnag.notify(new Throwable(TAG + ":reset watchTimes error:" + msg), Severity.WARNING);
                    }
                });
    }

    private UnLimit91PornItem saveVideoUrl(VideoResult videoResult, UnLimit91PornItem unLimit91PornItem) {
        dataBaseManager.insertOrReplaceInTx(videoResult);
        unLimit91PornItem.setVideoResult(videoResult);
        unLimit91PornItem.setViewHistoryDate(new Date());
        dataBaseManager.insertOrReplaceInTx(unLimit91PornItem);
        return unLimit91PornItem;
    }

    @Override
    public void downloadVideo(UnLimit91PornItem unLimit91PornItem, boolean isDownloadNeedWifi, boolean isForceReDownload) {
        downloadPresenter.downloadVideo(unLimit91PornItem, isDownloadNeedWifi, isForceReDownload, new DownloadPresenter.DownloadListener() {
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
