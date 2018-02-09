package com.u91porn.di.component;

import com.u91porn.di.PerActivity;
import com.u91porn.di.module.ActivityModule;
import com.u91porn.ui.about.AboutActivity;
import com.u91porn.ui.basemain.BaseMainFragment;
import com.u91porn.ui.download.DownloadActivity;
import com.u91porn.ui.download.DownloadingFragment;
import com.u91porn.ui.download.FinishedFragment;
import com.u91porn.ui.favorite.FavoriteActivity;
import com.u91porn.ui.images.meizitu.MeiZiTuFragment;
import com.u91porn.ui.images.mm99.Mm99Fragment;
import com.u91porn.ui.images.viewimage.PictureViewerActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.mine.MineFragment;
import com.u91porn.ui.pigav.PigAvFragment;
import com.u91porn.ui.pigav.playpigav.PlayPigAvActivity;
import com.u91porn.ui.porn91forum.Forum91IndexFragment;
import com.u91porn.ui.porn91forum.ForumFragment;
import com.u91porn.ui.porn91forum.browse91porn.Browse91PornActivity;
import com.u91porn.ui.porn91video.author.AuthorActivity;
import com.u91porn.ui.porn91video.common.CommonFragment;
import com.u91porn.ui.porn91video.index.IndexFragment;
import com.u91porn.ui.porn91video.play.BasePlayVideo;
import com.u91porn.ui.porn91video.recentupdates.RecentUpdatesFragment;
import com.u91porn.ui.porn91video.search.SearchActivity;
import com.u91porn.ui.proxy.ProxySettingActivity;
import com.u91porn.ui.setting.SettingActivity;
import com.u91porn.ui.splash.SplashActivity;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.ui.user.UserRegisterActivity;

import dagger.Component;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity splashActivity);

    void inject(MainActivity mainActivity);

    void inject(DownloadActivity downloadActivity);

    void inject(SettingActivity settingActivity);

    void inject(AboutActivity aboutActivity);

    void inject(FavoriteActivity favoriteActivity);

    void inject(SearchActivity searchActivity);

    void inject(BasePlayVideo basePlayVideo);

    void inject(UserLoginActivity userLoginActivity);

    void inject(UserRegisterActivity userRegisterActivity);

    void inject(AuthorActivity authorActivity);

    void inject(ProxySettingActivity proxySettingActivity);

    void inject(PlayPigAvActivity playPigAvActivity);

    void inject(PictureViewerActivity pictureViewerActivity);

    void inject(Browse91PornActivity browse91PornActivity);

    void inject(IndexFragment indexFragment);

    void inject(CommonFragment commonFragment);

    void inject(PigAvFragment pigAvFragment);

    void inject(MeiZiTuFragment meiZiTuFragment);

    void inject(Mm99Fragment mm99Fragment);

    void inject(RecentUpdatesFragment recentUpdatesFragment);

    void inject(DownloadingFragment downloadingFragment);

    void inject(FinishedFragment finishedFragment);

    void inject(BaseMainFragment baseMainFragment);

    void inject(MineFragment mineFragment);

    void inject(ForumFragment forumFragment);

    void inject(Forum91IndexFragment forum91IndexFragment);
}
