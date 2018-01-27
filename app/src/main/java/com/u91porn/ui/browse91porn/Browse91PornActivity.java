package com.u91porn.ui.browse91porn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.data.ApiManager;
import com.u91porn.data.model.Content91Porn;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.eventbus.BaseUrlChangeEvent;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.viewimage.PictureViewerActivity;
import com.u91porn.utils.AppUtils;
import com.u91porn.data.model.HostJsScope;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SPUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SafeWebViewBridge.InjectedChromeClient;

/**
 * @author flymegoc
 */
public class Browse91PornActivity extends MvpActivity<Browse91View, Browse91Presenter> implements Browse91View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Forum91PronItem forum91PronItem;

    private ArrayList<String> imageList;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse91_porn);
        ButterKnife.bind(this);
        initToolBar(toolbar);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(
                new InjectedChromeClient("HostApp", HostJsScope.class)
        );
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("tid=")) {
                    showMessage(url, TastyToast.INFO);
                    int starIndex = url.indexOf("tid=");
                    int endIndex = url.indexOf("&");
                    if (starIndex > 0 && endIndex > 0 && endIndex < url.length()) {
                        String tidStr = url.substring(starIndex + 4, endIndex);
                        presenter.loadContent(Long.parseLong(tidStr));
                    } else if (starIndex > 0) {
                        String tidStr = url.substring(starIndex + 4, url.length());
                        presenter.loadContent(Long.parseLong(tidStr));
                    }
                }
                return true;
            }
        });
        AppUtils.setColorSchemeColors(context, swipeLayout);
        forum91PronItem = (Forum91PronItem) getIntent().getSerializableExtra(Keys.KEY_INTENT_BROWSE_FORUM_91_PORN_ITEM);
        toolbar.setSubtitle(forum91PronItem.getTitle());
        presenter.loadContent(forum91PronItem.getTid());

        imageList = new ArrayList<>();
        boolean needShowTip = (boolean) SPUtils.get(this, Keys.KEY_SP_VIEW_91_PORN_FORUM_CONTENT_SHOW_TIP, true);
        if (needShowTip) {
            showTipDialog();
        }
    }

    private void showTipDialog() {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("1. 如果你看不到网页内容或者报错了，说明当前页面结构还不支持解析\n" +
                "2. 在你网速慢或者图片较大或者服务器响应慢的情况下，你会只看到少少的几行文字，这是正常的，你可以多刷新几次试试\n" +
                "3. 你可以点击某一张图进入浏览图片模式\n" +
                "4. 目前不支持查看评论及部分网页未适配屏幕宽度");
        builder.addAction("我知道了", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                SPUtils.put(Browse91PornActivity.this, Keys.KEY_SP_VIEW_91_PORN_FORUM_CONTENT_SHOW_TIP, false);
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        HostJsScope.setOnImageClick(onImageClick);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HostJsScope.removeOnImageClick();
    }

    private HostJsScope.OnImageClick onImageClick = new HostJsScope.OnImageClick() {
        @Override
        public void onClick(String imageUrl) {
            Intent intent = new Intent(Browse91PornActivity.this, PictureViewerActivity.class);
            intent.putExtra(Keys.KEY_INTENT_PICTURE_VIEWER_CURRENT_IMAGE_POSITION, imageList.indexOf(imageUrl));
            intent.putStringArrayListExtra(Keys.KEY_INTENT_PICTURE_VIEWER_IMAGE_ARRAY_LIST, imageList);
            startActivityWithAnimotion(intent);
        }
    };

    @NonNull
    @Override
    public Browse91Presenter createPresenter() {
        return new Browse91Presenter(ApiManager.getInstance().getForum91PronServiceApi());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBaseUrlChangeEvent(BaseUrlChangeEvent baseUrlChangeEvent) {
        presenter.setForum91PronServiceApi(ApiManager.getInstance().getForum91PronServiceApi());
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        swipeLayout.setRefreshing(pullToRefresh);
    }

    @Override
    public void showContent() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        swipeLayout.setRefreshing(false);
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void loadContentSuccess(Content91Porn content91Porn) {
        imageList.clear();
        imageList.addAll(content91Porn.getImageList());
        webview.loadData(content91Porn.getContent(), "text/html; charset=UTF-8", null);
    }

    @Override
    public void onRefresh() {
        presenter.loadContent(forum91PronItem.getTid());
    }
}
