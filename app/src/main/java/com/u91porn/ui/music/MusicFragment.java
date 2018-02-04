package com.u91porn.ui.music;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u91porn.R;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.BaseFragment;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * A simple {@link Fragment} subclass.
 * @author flymegoc
 */
public class MusicFragment extends BaseFragment {


    public MusicFragment() {
        // Required empty public constructor
    }

    public static MusicFragment getInstance() {
        return new MusicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pigav.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PigAvServiceApi pigAvServiceApi = retrofit.create(PigAvServiceApi.class);
        pigAvServiceApi.parseVideoUrl("https://pigav.com/218473/%E8%8B%A5%E8%8F%9C%E5%A5%88%E5%A4%AE-%E5%A5%B3%E6%95%99%E5%B8%AB%E4%B8%AD%E5%87%BA20%E9%80%A3%E7%99%BA.html")
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(String msg, int code) {

                    }
                });
    }

    interface PigAvServiceApi {
        @GET
        Observable<String> parseVideoUrl(@Url String url);
    }
}
