package com.u91porn.ui.play;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.danikula.videocache.HttpProxyCacheServer;
import com.github.rubensousa.floatingtoolbar.FloatingToolbar;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.adapter.VideoCommentAdapter;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.User;
import com.u91porn.data.model.VideoComment;
import com.u91porn.data.model.VideoResult;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.author.AuthorActivity;
import com.u91porn.ui.download.DownloadPresenter;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.Keys;
import com.u91porn.utils.LoadHelperUtils;
import com.u91porn.utils.SPUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

/**
 * @author flymegoc
 */
public abstract class BasePlayVideo extends MvpActivity<PlayVideoView, PlayVideoPresenter> implements PlayVideoView {

    private final String TAG = BasePlayVideo.class.getSimpleName();

    @BindView(R.id.recyclerView_video_comment)
    RecyclerView recyclerViewVideoComment;
    @BindView(R.id.floatingToolbar)
    FloatingToolbar floatingToolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.tv_play_video_title)
    TextView tvPlayVideoTitle;
    @BindView(R.id.tv_play_video_author)
    TextView tvPlayVideoAuthor;
    @BindView(R.id.tv_play_video_add_date)
    TextView tvPlayVideoAddDate;
    @BindView(R.id.tv_play_video_info)
    TextView tvPlayVideoInfo;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.fl_load_holder)
    FrameLayout flLoadHolder;
    @BindView(R.id.et_video_comment)
    AppCompatEditText etVideoComment;
    @BindView(R.id.iv_video_comment_send)
    ImageView ivVideoCommentSend;
    @BindView(R.id.et_comment_input_layout)
    LinearLayout etCommentInputLayout;
    @BindView(R.id.comment_swipeRefreshLayout)
    SwipeRefreshLayout commentSwipeRefreshLayout;
    @BindView(R.id.userinfo_layout)
    LinearLayout userinfoLayout;
    @BindView(R.id.videoplayer_container)
    FrameLayout videoplayerContainer;

    private AlertDialog mAlertDialog;
    private AlertDialog favoriteDialog;
    private AlertDialog commentVideoDialog;
    HttpProxyCacheServer proxy = MyApplication.getInstace().getProxy();
    private LoadViewHelper helper;

    private UnLimit91PornItem unLimit91PornItem;
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
    private Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
    private CacheProviders cacheProviders = MyApplication.getInstace().getCacheProviders();
    private FavoritePresenter favoritePresenter = new FavoritePresenter(unLimit91PornItemBox, mNoLimit91PornServiceApi, cacheProviders, MyApplication.getInstace().getUser(), provider);
    private DownloadPresenter downloadPresenter = new DownloadPresenter(unLimit91PornItemBox, provider);
    private SharedPrefsCookiePersistor sharedPrefsCookiePersistor = MyApplication.getInstace().getSharedPrefsCookiePersistor();
    private SetCookieCache setCookieCache = MyApplication.getInstace().getSetCookieCache();

    private VideoCommentAdapter videoCommentAdapter;
    private boolean isVideoError = true;
    private boolean isComment = true;
    private VideoComment videoComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_play_video);
        ButterKnife.bind(this);
        ViewCompat.setElevation(etCommentInputLayout, 12);
        setVideoViewHeight(videoplayerContainer);
        initPlayerView();
        unLimit91PornItem = (UnLimit91PornItem) getIntent().getSerializableExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM);

        if (unLimit91PornItem == null) {
            showMessage("参数错误，无法解析", TastyToast.ERROR);
            return;
        }
        initListener();
        initDialog();
        initLoadHelper();
        initVideoComments();
        initData();
        initBottomMenu();
        setLoadingViewVideoCommentHeight();
    }

    public abstract void initPlayerView();

    private void initListener() {
        ivVideoCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etVideoComment.getText().toString().trim();
                commentOrReplyVideo(comment);
            }
        });
        commentSwipeRefreshLayout.setEnabled(false);
        commentSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (unLimit91PornItem.getVideoResult() == null || unLimit91PornItem.getVideoResult().getTarget() == null) {
                    commentSwipeRefreshLayout.setRefreshing(false);
                    return;
                }
                String videoId = unLimit91PornItem.getVideoResult().getTarget().getVideoId();
                presenter.loadVideoComment(videoId, true, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
            }
        });
        userinfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isComment = true;
                videoCommentAdapter.setClickPosition(-1);
                videoCommentAdapter.notifyDataSetChanged();
                etVideoComment.setHint(R.string.comment_video_hint_tip);
            }
        });
    }

    /**
     * 评论视频或者回复评论
     *
     * @param comment 留言内容
     */
    private void commentOrReplyVideo(String comment) {
        if (TextUtils.isEmpty(comment)) {
            showMessage("请填写评论", TastyToast.INFO);
            return;
        }
        User user = MyApplication.getInstace().getUser();
        if (user == null) {
            showMessage("请先登录帐号", TastyToast.INFO);
            goToLogin();
            return;
        }
        String vid = unLimit91PornItem.getVideoResult().getTarget().getVideoId();
        String uid = String.valueOf(user.getUserId());
        if (isComment) {
            commentVideoDialog.show();
            presenter.commentVideo(comment, uid, vid, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
        } else {
            if (videoComment == null) {
                showMessage("请先选择需要回复的评论！", TastyToast.INFO);
                return;
            }
            commentVideoDialog.show();
            String username = videoComment.getuName();
            String commentId = videoComment.getReplyId();
            presenter.replyComment(comment, username, vid, commentId, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
        }
    }

    private void initData() {
        UnLimit91PornItem tmp = BoxQureyHelper.findByViewKey(unLimit91PornItem.getViewKey());
        if (tmp == null || tmp.getVideoResult().getTarget() == null || TextUtils.isEmpty(tmp.getVideoResult().getTarget().getVideoUrl()) || TextUtils.isEmpty(tmp.getVideoResult().getTarget().getOwnnerName())) {
            presenter.loadVideoUrl(unLimit91PornItem.getViewKey(), HeaderUtils.getIndexHeader());
        } else {
            videoplayerContainer.setVisibility(View.VISIBLE);
            Logger.t(TAG).d("使用已有播放地址");
            //showMessage("使用已有播放地址", TastyToast.SUCCESS);
            //浏览历史
            tmp.setViewHistoryDate(new Date());
            unLimit91PornItemBox.put(tmp);

            unLimit91PornItem.setVideoResult(tmp.getVideoResult());
            setToolBarLayoutInfo(unLimit91PornItem);
            playVideo(unLimit91PornItem.getTitle(), unLimit91PornItem.getVideoResult().getTarget().getVideoUrl(), "", "");
            //加载评论
            presenter.loadVideoComment(tmp.getVideoResult().getTarget().getVideoId(), true, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
        }
    }

    private void setToolBarLayoutInfo(final UnLimit91PornItem unLimit91PornItem) {
        tvPlayVideoTitle.setText(unLimit91PornItem.getTitle());
        VideoResult videoResult = unLimit91PornItem.getVideoResult().getTarget();
        tvPlayVideoAuthor.setText(videoResult.getOwnnerName());
        tvPlayVideoAddDate.setText(videoResult.getAddDate());
        tvPlayVideoInfo.setText(videoResult.getUserOtherInfo());
        tvPlayVideoAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = MyApplication.getInstace().getUser();
                if (user == null) {
                    goToLogin();
                    showMessage("请先登录", TastyToast.INFO);
                    return;
                }
                if (unLimit91PornItem.getVideoResult() == null || unLimit91PornItem.getVideoResult().getTarget() == null) {
                    showMessage("视频还未解析成功！", TastyToast.INFO);
                    return;
                }
                Intent intent = new Intent(BasePlayVideo.this, AuthorActivity.class);
                intent.putExtra(Keys.KEY_INTENT_UID, unLimit91PornItem.getVideoResult().getTarget().getOwnnerId());
                startActivityForResultWithAnimotion(intent, 1);
            }
        });
    }

    private void initLoadHelper() {
        helper = new LoadViewHelper(flLoadHolder);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                if (isVideoError) {
                    presenter.loadVideoUrl(unLimit91PornItem.getViewKey(), HeaderUtils.getIndexHeader());
                } else {
                    //加载评论
                    presenter.loadVideoComment(unLimit91PornItem.getVideoResult().getTarget().getVideoId(), true, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
                }
            }
        });
    }

    private void initDialog() {
        mAlertDialog = DialogUtils.initLodingDialog(this, "视频地址解析中...");
        favoriteDialog = DialogUtils.initLodingDialog(this, "收藏中,请稍后...");
        commentVideoDialog = DialogUtils.initLodingDialog(this, "提交评论中,请稍后...");
    }

    /**
     * 测量高度，为了让loading居中显示
     */
    private void setLoadingViewVideoCommentHeight() {
        int height = (int) SPUtils.get(this, Keys.KEY_SP_APPBARLAYOUT_HEIGHT, 0);
        if (height > 0) {
            setLoadingViewHeight(height);
        }
        appBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                appBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = appBarLayout.getMeasuredHeight();
                int screenHeight = QMUIDisplayHelper.getScreenHeight(BasePlayVideo.this);
                int remainHeight = screenHeight - height;
                if (remainHeight != height) {
                    SPUtils.put(BasePlayVideo.this, Keys.KEY_SP_APPBARLAYOUT_HEIGHT, remainHeight);
                    setLoadingViewHeight(remainHeight);
                }
            }
        });
    }

    /**
     * 控制高度
     */
    private void setLoadingViewHeight(int height) {
        Logger.t(TAG).d("height:" + height);
        ViewGroup.LayoutParams layoutParams = flLoadHolder.getLayoutParams();
        layoutParams.height = height;
        flLoadHolder.setLayoutParams(layoutParams);
    }

    private void initVideoComments() {

        List<VideoComment> videoCommentList = new ArrayList<>();
        videoCommentAdapter = new VideoCommentAdapter(this, R.layout.item_video_comment, videoCommentList);

        recyclerViewVideoComment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVideoComment.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerViewVideoComment.setAdapter(videoCommentAdapter);
        videoCommentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                //加载评论
                presenter.loadVideoComment(unLimit91PornItem.getVideoResult().getTarget().getVideoId(), false, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
            }
        }, recyclerViewVideoComment);
        videoCommentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (floatingToolbar.isShowing()) {
                    floatingToolbar.hide();
                }
                isComment = false;
                videoCommentAdapter.setClickPosition(position);
                videoCommentAdapter.notifyDataSetChanged();
                videoComment = (VideoComment) adapter.getData().get(position);
                etVideoComment.setHint("回复：" + videoComment.getuName());
            }
        });

    }

    private void initBottomMenu() {
        floatingToolbar.attachFab(fab);
        floatingToolbar.attachRecyclerView(recyclerViewVideoComment);
        floatingToolbar.setClickListener(new FloatingToolbar.ItemClickListener() {
            @Override
            public void onItemClick(MenuItem item) {
                onOptionsItemSelected(item);
            }

            @Override
            public void onItemLongClick(MenuItem item) {

            }
        });
    }

    /**
     * 根据屏幕宽度信息重设videoview宽高为16：9比例
     */
    protected void setVideoViewHeight(View playerView) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        layoutParams.height = QMUIDisplayHelper.getScreenWidth(this) * 9 / 16;
        playerView.setLayoutParams(layoutParams);
    }

    public abstract void playVideo(String title, String videoUrl, String name, String thumImgUrl);


    @NonNull
    @Override
    public PlayVideoPresenter createPresenter() {
        return new PlayVideoPresenter(mNoLimit91PornServiceApi, favoritePresenter, downloadPresenter, sharedPrefsCookiePersistor, setCookieCache, cacheProviders, provider);
    }

    @Override
    public void showParsingDialog() {
        if (mAlertDialog == null) {
            return;
        }
        mAlertDialog.show();
    }

    @Override
    public void playVideo(VideoResult videoResult) {
        videoplayerContainer.setVisibility(View.VISIBLE);
        dismissDialog();
        //showMessage("解析成功，开始播放", TastyToast.SUCCESS);
        presenter.saveVideoUrl(videoResult, unLimit91PornItem);
        helper.showContent();
        unLimit91PornItem.videoResult.setTarget(videoResult);
        setToolBarLayoutInfo(unLimit91PornItem);
        playVideo(unLimit91PornItem.getTitle(), videoResult.getVideoUrl(), "", videoResult.getThumbImgUrl());
        presenter.loadVideoComment(videoResult.getVideoId(), true, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
        showTopMessage("Tips：目前大多数视频需要挂代理才能观看的！");
    }

    @Override
    public void errorParseVideoUrl(String errorMessage) {
        dismissDialog();
        isVideoError = true;
        helper.showError();
        LoadHelperUtils.setErrorText(helper.getLoadError(), R.id.tv_error_text, "解析视频地址失败了，点击重试");
        showMessage(errorMessage, TastyToast.ERROR);
    }

    @Override
    public void favoriteSuccess() {
        SPUtils.put(this, Keys.KEY_SP_USER_FAVORITE_NEED_REFRESH, true);
        showMessage("收藏成功", TastyToast.SUCCESS);
    }

    @Override
    public void setVideoCommentData(List<VideoComment> videoCommentList, boolean pullToRefresh) {
        if (pullToRefresh) {
            recyclerViewVideoComment.smoothScrollToPosition(0);
        }
        videoCommentAdapter.setNewData(videoCommentList);
        commentSwipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void setMoreVideoCommentData(List<VideoComment> videoCommentList) {
        videoCommentAdapter.loadMoreComplete();
        videoCommentAdapter.addData(videoCommentList);
    }

    @Override
    public void noMoreVideoCommentData(String message) {
        videoCommentAdapter.loadMoreEnd(true);
        showMessage(message, TastyToast.INFO);
    }

    @Override
    public void loadMoreVideoCommentError(String message) {
        videoCommentAdapter.loadMoreFail();
    }

    @Override
    public void loadVideoCommentError(String message) {
        isVideoError = false;
        helper.showError();
        LoadHelperUtils.setErrorText(helper.getLoadError(), R.id.tv_error_text, "加载评论失败了，点击重试");
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void commentVideoSuccess(String message) {
        cleanVideoCommentInput();
        reFreshData();
        showMessage(message, TastyToast.SUCCESS);
    }

    @Override
    public void commentVideoError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void replyVideoCommentSuccess(String message) {
        cleanVideoCommentInput();
        isComment = true;
        etVideoComment.setHint(R.string.comment_video_hint_tip);
        videoCommentAdapter.setClickPosition(-1);
        reFreshData();
        showMessage(message, TastyToast.SUCCESS);
    }

    private void reFreshData() {
        //刷新
        commentSwipeRefreshLayout.setRefreshing(true);
        String videoId = unLimit91PornItem.getVideoResult().getTarget().getVideoId();
        presenter.loadVideoComment(videoId, true, HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
    }

    @Override
    public void replyVideoCommentError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    private void cleanVideoCommentInput() {
        etVideoComment.setText("");
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
        dismissDialog();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "拼命加载评论中...");
    }

    @Override
    public void showContent() {
        if (videoCommentAdapter.getData().size() == 0) {
            isVideoError = false;
            helper.showEmpty();
            LoadHelperUtils.setEmptyText(helper.getLoadEmpty(), R.id.tv_empty_info, "暂无评论");
        } else {
            flLoadHolder.setVisibility(View.GONE);
            helper.showContent();
        }
        commentSwipeRefreshLayout.setRefreshing(false);
        dismissDialog();
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
        dismissDialog();
    }

    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing() && !isFinishing()) {
            mAlertDialog.dismiss();
        }
        if (favoriteDialog != null && favoriteDialog.isShowing() && !isFinishing()) {
            favoriteDialog.dismiss();
        }
        if (commentVideoDialog != null && commentVideoDialog.isShowing() && !isFinishing()) {
            commentVideoDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.playvideo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_play_collect) {
            VideoResult videoResult = unLimit91PornItem.getVideoResult().getTarget();
            if (videoResult == null) {
                showMessage("还未成功解析视频链接，不能收藏！", TastyToast.INFO);
                return true;
            }
            User user = MyApplication.getInstace().getUser();
            if (user == null) {
                goToLogin();
                showMessage("请先登录", TastyToast.SUCCESS);
                return true;
            }
            if (Integer.parseInt(videoResult.getOwnnerId()) == user.getUserId()) {
                showMessage("不能收藏自己的视频", TastyToast.WARNING);
                return true;
            }
            favoriteDialog.show();
            presenter.favorite("addToFavorites", String.valueOf(user.getUserId()), videoResult.getVideoId(), videoResult.getOwnnerId(), "json", HeaderUtils.getPlayVideoReferer(unLimit91PornItem.getViewKey()));
            return true;
        } else if (id == R.id.menu_play_download) {
            presenter.downloadVideo(unLimit91PornItem);
            return true;
        } else if (id == R.id.menu_play_share) {
            if (unLimit91PornItem.getVideoResult() == null || unLimit91PornItem.getVideoResult().getTarget() == null) {
                showMessage("还未成功解析视频链接，不能分享！", TastyToast.INFO);
                return true;
            }
            String url = unLimit91PornItem.getVideoResult().getTarget().getVideoUrl();
            if (TextUtils.isEmpty(url)) {
                showMessage("还未成功解析视频链接，不能分享！", TastyToast.INFO);
                return true;
            }
            Intent textIntent = new Intent(Intent.ACTION_SEND);
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, "链接：" + url);
            startActivity(Intent.createChooser(textIntent, "分享视频地址"));
            return true;
        } else if (id == R.id.menu_play_comment) {
            showMessage("向下滑动即可评论", TastyToast.INFO);
            return true;
        } else if (id == R.id.menu_play_close) {
            floatingToolbar.hide();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivityWithAnimotion(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AuthorActivity.AUTHORACTIVITY_RESULT_CODE) {
            unLimit91PornItem = (UnLimit91PornItem) data.getSerializableExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM);
            recyclerViewVideoComment.smoothScrollToPosition(0);
            videoCommentAdapter.getData().clear();
            videoCommentAdapter.notifyDataSetChanged();
            initData();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
