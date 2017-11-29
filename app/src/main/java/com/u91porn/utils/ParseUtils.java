package com.u91porn.utils;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class ParseUtils {

    /**
     * 解析主页
     *
     * @param html 主页html
     * @return 视频列表
     */
    public static List<UnLimit91PornItem> parseIndex(String html) {
        List<UnLimit91PornItem> unLimit91PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("tab-featured");
        Elements itms = body.select("p");
        for (Element element : itms) {
            UnLimit91PornItem unLimit91PornItem = new UnLimit91PornItem();

            String title = element.getElementsByClass("title").first().text();
            unLimit91PornItem.setTitle(title);
            Logger.d(title);

            String imgUrl = element.select("img").first().attr("src");
            unLimit91PornItem.setImgUrl(imgUrl);
            Logger.d(imgUrl);

            String duration = element.getElementsByClass("duration").first().text();
            unLimit91PornItem.setDuration(duration);
            Logger.d(duration);

            String contentUrl = element.select("a").first().attr("href");
            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            unLimit91PornItem.setViewKey(viewKey);
            Logger.d(viewKey);

            String allInfo = element.text();
            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);

            unLimit91PornItem.setInfo(info);
            Logger.d(info);
            unLimit91PornItemList.add(unLimit91PornItem);
        }
        return unLimit91PornItemList;
    }

    /**
     * 解析其他类别
     *
     * @param html 类别
     * @return 列表
     */
    public static BaseResult parseHot(String html) {
        int totalPage = 1;
        List<UnLimit91PornItem> unLimit91PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("fullside");

        Elements listchannel = body.getElementsByClass("listchannel");
        for (Element element : listchannel) {
            UnLimit91PornItem unLimit91PornItem = new UnLimit91PornItem();
            String contentUrl = element.select("a").first().attr("href");
            Logger.d(contentUrl);
            contentUrl = contentUrl.substring(0, contentUrl.indexOf("&"));
            Logger.d(contentUrl);
            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            unLimit91PornItem.setViewKey(viewKey);

            String imgUrl = element.select("a").first().select("img").first().attr("src");
            Logger.d(imgUrl);
            unLimit91PornItem.setImgUrl(imgUrl);

            String title = element.select("a").first().select("img").first().attr("title");
            Logger.d(title);
            unLimit91PornItem.setTitle(title);


            String allInfo = element.text();

            int sindex = allInfo.indexOf("时长");

            String duration = allInfo.substring(sindex + 3, sindex + 8);
            unLimit91PornItem.setDuration(duration);

            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);
            unLimit91PornItem.setInfo(info.replace("还未被评分", ""));
            Logger.d(info);

            unLimit91PornItemList.add(unLimit91PornItem);
        }
        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() > 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                Logger.d("总页数：" + totalPage);
            }
        }
        BaseResult baseResult = new BaseResult();
        baseResult.setTotalPage(totalPage);
        baseResult.setUnLimit91PornItemList(unLimit91PornItemList);
        return baseResult;
    }

    /**
     * 解析视频播放连接
     *
     * @param html 视频页
     * @return 视频连接
     */
    public static String parseVideoPlayUrl(String html) {
        if (html.contains("你每天只可观看10个视频")) {
            Logger.d("已经超出观看上限了");
            return "";
        }
        Document doc = Jsoup.parse(html);
        String videoUrl = doc.select("video").first().select("source").first().attr("src");
        Logger.d("视频链接：" + videoUrl);
        String thumImg = doc.getElementById("vid").attr("poster");
        Logger.d("缩略图：" + thumImg);
        return videoUrl;
    }
}
