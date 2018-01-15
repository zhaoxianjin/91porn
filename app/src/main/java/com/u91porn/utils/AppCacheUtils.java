package com.u91porn.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.format.Formatter;

import java.io.File;

/**
 * 应用缓存
 *
 * @author flymegoc
 * @date 2018/1/13
 */

public class AppCacheUtils {
    public final static long MAX_VIDEO_CACHE_SIZE = 800 * 1024 * 1024;
    private final static String RXCACHE_DIR = "/rxcache";
    private final static String VIDEO_CACHE_DIR = "/video-cache";
    private final static String GLIDE_DISCACHE_DIR = "/glide_cache_dir";

    /**
     * 获取RxCache 缓存目录
     *
     * @param context context
     * @return 缓存目录
     */
    @NonNull
    public static File getRxCacheDir(Context context) {
        String path;
        if (SDCardUtils.isSDCardMounted()) {
            path = context.getExternalCacheDir() + RXCACHE_DIR;
        } else {
            path = context.getCacheDir() + RXCACHE_DIR;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsoluteFile();
    }

    /**
     * 获取视频缓存目录
     *
     * @param context cotext
     * @return 缓存目录
     */
    @NonNull
    public static File getVideoCacheDir(Context context) {
        String path;
        if (SDCardUtils.isSDCardMounted()) {
            path = context.getExternalCacheDir() + VIDEO_CACHE_DIR;
        } else {
            path = context.getCacheDir() + VIDEO_CACHE_DIR;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsoluteFile();
    }

    /**
     * 获取glide缓存目录
     *
     * @param context context
     * @return 缓存目录
     */
    public static File getGlideDiskCacheDir(Context context) {
        String path;
        if (SDCardUtils.isSDCardMounted()) {
            path = context.getExternalCacheDir() + GLIDE_DISCACHE_DIR;
        } else {
            path = context.getCacheDir() + GLIDE_DISCACHE_DIR;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsoluteFile();
    }

    /**
     * 获取RxCache缓存大小
     *
     * @param context context
     * @return 缓存大小
     */
    public static String getGlidecacheFileSizeStr(Context context) {
        long fileSize = getGlidecacheFileSizeNum(context);
        return Formatter.formatFileSize(context, fileSize);
    }

    private static long getGlidecacheFileSizeNum(Context context) {
        long fileSize = 0;
        File file = getGlideDiskCacheDir(context);
        for (File childFile : file.listFiles()) {
            fileSize += childFile.length();
        }
        return fileSize;
    }

    /**
     * 获取RxCache缓存大小
     *
     * @param context context
     * @return 缓存大小
     */
    public static String getRxcacheFileSizeStr(Context context) {
        long fileSize = getRxcacheFileSizeNum(context);
        return Formatter.formatFileSize(context, fileSize);
    }

    private static long getRxcacheFileSizeNum(Context context) {
        long fileSize = 0;
        File file = getRxCacheDir(context);
        for (File childFile : file.listFiles()) {
            fileSize += childFile.length();
        }
        return fileSize;
    }

    /**
     * 获取videoCache缓存大小
     *
     * @param context context
     * @return 缓存大小
     */
    public static String getVideoCacheFileSizeStr(Context context) {
        long fileSize = getVideoCacheFileSizeNum(context);
        return Formatter.formatFileSize(context, fileSize);
    }

    private static long getVideoCacheFileSizeNum(Context context) {
        long fileSize = 0;
        File file = getVideoCacheDir(context);
        for (File childFile : file.listFiles()) {
            fileSize += childFile.length();
        }
        return fileSize;
    }

    public static String getAllCacheFileSizeStr(Context context) {
        long fileSize = getRxcacheFileSizeNum(context) + getVideoCacheFileSizeNum(context) + getGlidecacheFileSizeNum(context);
        return Formatter.formatFileSize(context, fileSize);
    }

    public static boolean cleanRxCache(Context context) {

        File fileDir = getVideoCacheDir(context);
        return deleteDirFile(fileDir);
    }

    public static boolean cleanVideoCache(Context context) {

        File fileDir = getVideoCacheDir(context);
        return deleteDirFile(fileDir);
    }

    public static boolean cleanAllCache(Context context) {
        return cleanRxCache(context) && cleanVideoCache(context);
    }

    public static boolean cleanCacheFile(File fileDir) {
        return deleteDirFile(fileDir);
    }

    public static boolean cleanAllCacheFile(File[] fileDirs) {
        boolean result = true;
        for (File file : fileDirs) {
            result = deleteDirFile(file);
        }
        return result;
    }

    /**
     * 仅删除目录下的文件
     *
     * @param fileDir 目录
     * @return boolean
     */
    private static boolean deleteDirFile(File fileDir) {
        boolean result = true;
        if (!fileDir.isDirectory()) {
            return false;
        }
        for (File childFile : fileDir.listFiles()) {
            if (childFile.isFile()) {
                result = childFile.delete();
            }
        }
        return result;
    }

    /**
     * 递归删除目录下所有文件
     *
     * @param dir 目录
     * @return boolean
     */
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        } else if (dir == null) {
            return false;
        }
        return dir.delete();
    }
}
