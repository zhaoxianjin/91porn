/*
 * Copyright (C) 2016 Brian Wernick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.u91porn.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener;
import com.devbrackets.android.exomedia.ui.animation.BottomViewHideShowAnimation;
import com.devbrackets.android.exomedia.ui.animation.TopViewHideShowAnimation;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.devbrackets.android.exomedia.util.TimeFormatUtil;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.u91porn.R;
import com.u91porn.animation.RightViewHideShowAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jzvd.JZUtils;

/**
 * Provides playback controls for the {@link VideoView} on Mobile
 * (Phone, Tablet, etc.) devices.
 */
@SuppressWarnings("unused")
public class VideoControlsMobile extends VideoControls {
    protected SeekBar seekBar;
    protected ImageView fullScreenImageView;
    protected TextView speedTextView;
    protected ProgressBar bottomProgressBar;
    protected boolean userInteracting = false;
    protected ViewGroup parentViewGroup;
    protected boolean isFullScreen = false;
    protected FullScreenListener fullScreenListener = new FullScreenListener();

    protected boolean isPlayComplete = false;
    protected ViewGroup speedPanelContainner;
    protected ListView speedListView;

    public VideoControlsMobile(Context context) {
        super(context);
    }

    public VideoControlsMobile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoControlsMobile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoControlsMobile(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.exoplayer_controller_layout;
    }

    @Override
    public void setVideoView(@Nullable final VideoView videoView) {
        super.setVideoView(videoView);
        if (videoView != null && videoView.getVideoControls() != null) {
            videoView.getVideoControls().setVisibilityListener(new ControlsVisibilityListener());
            videoView.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion() {
                    isPlayComplete = true;
                }
            });
        }
    }

    @Override
    protected void onPlayPauseClick() {
        if (isPlayComplete) {
            if (videoView != null) {
                isPlayComplete = false;
                videoView.restart();
            }
        } else {
            super.onPlayPauseClick();
        }
    }

    @Override
    public void setPosition(@IntRange(from = 0) long position) {
        currentTimeTextView.setText(TimeFormatUtil.formatMs(position));
        seekBar.setProgress((int) position);
        bottomProgressBar.setProgress((int) position);
    }

    @Override
    public void setDuration(@IntRange(from = 0) long duration) {
        if (duration != seekBar.getMax()) {
            endTimeTextView.setText(TimeFormatUtil.formatMs(duration));
            seekBar.setMax((int) duration);
        }
        if (duration != bottomProgressBar.getMax()) {
            bottomProgressBar.setMax((int) duration);
        }
    }

    @Override
    public void updateProgress(@IntRange(from = 0) long position, @IntRange(from = 0) long duration, @IntRange(from = 0, to = 100) int bufferPercent) {
        if (!userInteracting) {
            seekBar.setSecondaryProgress((int) (seekBar.getMax() * ((float) bufferPercent / 100)));
            seekBar.setProgress((int) position);
            currentTimeTextView.setText(TimeFormatUtil.formatMs(position));

            bottomProgressBar.setProgress((int) position);
            bottomProgressBar.setSecondaryProgress((int) (bottomProgressBar.getMax() * ((float) bufferPercent / 100)));
        }
    }

    @Override
    protected void setup(Context context) {
        super.setup(context);
        String[] listItems = new String[]{
                "3.0x",
                "2.5x",
                "2.0x",
                "1.5x",
                "1.0x",
                "0.5x",
        };
        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_exoplayer_controller_speed, data);
        speedListView.setAdapter(arrayAdapter);
    }

    @Override
    protected void retrieveViews() {
        super.retrieveViews();
        seekBar = findViewById(R.id.exomedia_controls_video_seek);
        fullScreenImageView = findViewById(R.id.exomedia_controls_full_screen);
        speedTextView = findViewById(R.id.exomedia_controls_speed);
        bottomProgressBar = findViewById(R.id.exomedia_controls_bottom_progress);
        speedPanelContainner = findViewById(R.id.exomedia_controls_right_speed_panel);
        speedListView = findViewById(R.id.exomedia_controls_right_speed_listview);

        FadingCircle fadingCircle = new FadingCircle();
        fadingCircle.setBounds(0, 0, 50, 50);
        loadingProgressBar.setIndeterminateDrawable(fadingCircle);
    }

    @Override
    protected void registerListeners() {
        super.registerListeners();
        seekBar.setOnSeekBarChangeListener(new SeekBarChanged());
        fullScreenImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFullScreen) {
                    goFullScreen();
                } else {
                    exitFullScreen();
                }
            }
        });
        speedTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView != null && !videoView.isPlaying()) {
                    return;
                }
                LayoutParams layoutParams = (LayoutParams) speedPanelContainner.getLayoutParams();
                layoutParams.bottomMargin = controlsContainer.getHeight();
                speedPanelContainner.setLayoutParams(layoutParams);
                show();
                hideDelayed(5 * 1000);
                speedPanelContainner.startAnimation(new RightViewHideShowAnimation(speedPanelContainner, true, CONTROL_VISIBILITY_ANIMATION_LENGTH));
            }
        });
        speedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (videoView != null) {
                    String item = (String) parent.getItemAtPosition(position);
                    videoView.setPlaybackSpeed(Float.parseFloat(item.replace("x", "")));
                    speedTextView.setText(item);
                }
                speedPanelContainner.startAnimation(new RightViewHideShowAnimation(speedPanelContainner, false, CONTROL_VISIBILITY_ANIMATION_LENGTH));
                hide();
            }
        });
    }

    protected void goFullScreen() {
        isFullScreen = true;
        fullScreenImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.jz_shrink));
        //setUiFlags(true);
        hideSystemUI();
        JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ViewGroup viewGroup = JZUtils.getWindow(getContext()).getDecorView().findViewById(android.R.id.content);
        parentViewGroup = (ViewGroup) videoView.getParent();
        parentViewGroup.removeView(videoView);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        viewGroup.addView(videoView, lp);

        RelativeLayout parent = (RelativeLayout) controlsContainer.getParent();
        ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
        layoutParams.width = QMUIDisplayHelper.getScreenWidth(getContext()) + 1000;
        parent.setLayoutParams(layoutParams);
    }

    protected void exitFullScreen() {
        isFullScreen = false;
        fullScreenImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.jz_enlarge));
        setUiFlags(false);
        JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewGroup viewGroup = JZUtils.getWindow(getContext()).getDecorView().findViewById(android.R.id.content);
        viewGroup.removeView(videoView);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        parentViewGroup.addView(videoView, 0, lp);
    }

    /**
     * Determines the appropriate fullscreen flags based on the
     * systems API version.
     *
     * @return The appropriate decor view flags to enter fullscreen mode when supported
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private int getFullscreenUiFlags() {
        int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        return flags;
    }

    // This snippet hides the system bars.
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        JZUtils.getWindow(getContext()).getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        JZUtils.getWindow(getContext()).getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * Listens to the system to determine when to show the default controls
     * for the {@link VideoView}
     */
    private class FullScreenListener implements OnSystemUiVisibilityChangeListener {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                if (videoView.getVideoControls() != null) {
                    videoView.showControls();
                }

            }
        }
    }

    /**
     * Applies the correct flags to the windows decor view to enter
     * or exit fullscreen mode
     *
     * @param fullscreen True if entering fullscreen mode
     */
    private void setUiFlags(boolean fullscreen) {
        View decorView = JZUtils.getWindow(getContext()).getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(fullscreen ? getFullscreenUiFlags() : View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /**
     * A Listener for the {@link VideoControls}
     * so that we can re-enter fullscreen mode when the controls are hidden.
     */
    private class ControlsVisibilityListener implements VideoControlsVisibilityListener {
        @Override
        public void onControlsShown() {
            // No additional functionality performed
        }

        @Override
        public void onControlsHidden() {
            if (isFullScreen) {
                hideSystemUI();
            }
        }
    }

    /**
     * Correctly sets up the fullscreen flags to avoid popping when we switch
     * between fullscreen and not
     */
    private void initUiFlags() {
        int flags = View.SYSTEM_UI_FLAG_VISIBLE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }

        View decorView = JZUtils.getWindow(getContext()).getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(fullScreenListener);
        }
    }

    public boolean onBackPressed() {
        if (isFullScreen) {
            exitFullScreen();
            return false;
        }
        return true;
    }

    @Override
    public void hideDelayed(long delay) {
        hideDelay = delay;

        if (delay < 0 || !canViewHide || isLoading) {
            return;
        }

        //If the user is interacting with controls we don't want to start the delayed hide yet
        if (!userInteracting) {
            visibilityHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateVisibility(false);
                }
            }, delay);
        }
    }

    @Override
    protected void animateVisibility(boolean toVisible) {
        if (isVisible == toVisible) {
            return;
        }

        if (!hideEmptyTextContainer || !isTextContainerEmpty()) {
            textContainer.startAnimation(new TopViewHideShowAnimation(textContainer, toVisible, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }

        if (!isLoading) {
            controlsContainer.startAnimation(new BottomViewHideShowAnimation(controlsContainer, toVisible, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }
        if (isVisible) {
            playPauseButton.setVisibility(GONE);
            if (speedPanelContainner.getVisibility() == View.VISIBLE) {
                speedPanelContainner.startAnimation(new RightViewHideShowAnimation(speedPanelContainner, false, CONTROL_VISIBILITY_ANIMATION_LENGTH));
            }
            bottomProgressBar.setVisibility(VISIBLE);
        } else {
            playPauseButton.setVisibility(VISIBLE);
            bottomProgressBar.setVisibility(GONE);
        }
        isVisible = toVisible;
        onVisibilityChanged();
    }

    @Override
    protected void updateTextContainerVisibility() {
        if (!isVisible) {
            return;
        }

        boolean emptyText = isTextContainerEmpty();
        if (hideEmptyTextContainer && emptyText && textContainer.getVisibility() == VISIBLE) {
            textContainer.clearAnimation();
            textContainer.startAnimation(new TopViewHideShowAnimation(textContainer, false, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        } else if ((!hideEmptyTextContainer || !emptyText) && textContainer.getVisibility() != VISIBLE) {
            textContainer.clearAnimation();
            textContainer.startAnimation(new TopViewHideShowAnimation(textContainer, true, CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }
    }

    @Override
    public void showLoading(boolean initialLoad) {
        if (isLoading) {
            return;
        }

        isLoading = true;
        loadingProgressBar.setVisibility(View.VISIBLE);
        playPauseButton.setVisibility(GONE);
        if (initialLoad) {
            controlsContainer.setVisibility(View.GONE);
        } else {
            playPauseButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }

        show();
    }

    @Override
    public void finishLoading() {
        if (!isLoading) {
            return;
        }

        isLoading = false;
        loadingProgressBar.setVisibility(View.GONE);
        controlsContainer.setVisibility(View.VISIBLE);
        playPauseButton.setVisibility(VISIBLE);
        bottomProgressBar.setVisibility(GONE);
        playPauseButton.setEnabled(true);
        previousButton.setEnabled(enabledViews.get(R.id.exomedia_controls_previous_btn, true));
        nextButton.setEnabled(enabledViews.get(R.id.exomedia_controls_next_btn, true));

        updatePlaybackState(videoView != null && videoView.isPlaying());
    }

    @Override
    protected void updateButtonDrawables() {
        super.updateButtonDrawables();
        pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.jz_click_pause_selector);
        playDrawable = ContextCompat.getDrawable(getContext(), R.drawable.jz_click_play_selector);
    }

    /**
     * Listens to the seek bar change events and correctly handles the changes
     */
    protected class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        private long seekToTime;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            seekToTime = progress;
            if (currentTimeTextView != null) {
                currentTimeTextView.setText(TimeFormatUtil.formatMs(seekToTime));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            userInteracting = true;
            if (seekListener == null || !seekListener.onSeekStarted()) {
                internalListener.onSeekStarted();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            userInteracting = false;
            if (seekListener == null || !seekListener.onSeekEnded(seekToTime)) {
                internalListener.onSeekEnded(seekToTime);
            }
        }
    }
}