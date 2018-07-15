package com.u91porn.ui.pigav.playpigav;

import com.u91porn.data.model.PigAv;
import com.u91porn.data.model.PigAvVideo;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public interface PlayPigAvView extends BaseView {
    void playVideo(PigAvVideo pigAvVideo);

    void listVideo(List<PigAv> pigAvList);
}
