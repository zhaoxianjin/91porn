package com.u91porn.ui.about;

import android.content.Context;

import java.io.File;
import java.util.List;

/**
 * @author flymegoc
 * @date 2017/12/23
 */

public interface IAbout extends IBaseAbout {

    /**
     * 删除缓存文件
     *
     * @param fileDirList 缓存目录集
     */
    void cleanCacheFile(List<File> fileDirList, Context context);

    void countCacheFileSize(Context context, String title);
}
